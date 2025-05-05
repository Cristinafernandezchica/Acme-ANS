
package acme.features.customer.dashboard;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.forms.CustomerDashboard;
import acme.realms.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	@Autowired
	private CustomerDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CustomerDashboard dashboard = new CustomerDashboard();

		// 1. Inicializar todas las listas y mapas para evitar nulls
		dashboard.setLastFiveDestinations(new ArrayList<>());
		dashboard.setMoneySpentLastYear(new ArrayList<>());
		dashboard.setBookingCountByTravelClass("");
		dashboard.setBookingAverageCostLastFiveYears(new ArrayList<>());
		dashboard.setBookingMinCostLastFiveYears(new ArrayList<>());
		dashboard.setBookingMaxCostLastFiveYears(new ArrayList<>());
		dashboard.setBookingStdDevCostLastFiveYears(new ArrayList<>());

		// 2. Last five destinations
		List<String> destinations = this.repository.findLastFiveDestinations(customerId);
		if (destinations != null && !destinations.isEmpty())
			dashboard.setLastFiveDestinations(destinations.stream().limit(5).collect(Collectors.toList()));

		// 3. Money spent last year
		List<Object[]> moneySpentData = this.repository.findMoneySpentLastYear(customerId);
		if (moneySpentData != null)
			for (Object[] row : moneySpentData)
				if (row != null && row.length >= 2) {
					Money money = new Money();
					money.setCurrency(row[0] != null ? (String) row[0] : "EUR");
					money.setAmount(row[1] != null ? (Double) row[1] : 0.0);
					dashboard.getMoneySpentLastYear().add(money);
				}

		// 3. Booking count por clase
		List<Object[]> bookingData = this.repository.findBookingCountByTravelClass(customerId);
		StringBuilder bookingCountText = new StringBuilder();
		if (bookingData != null)
			for (Object[] row : bookingData)
				if (row != null && row.length >= 2)
					bookingCountText.append(row[0].toString()).append(": ").append(row[1].toString()).append("\n");
		dashboard.setBookingCountByTravelClass(bookingCountText.length() > 0 ? bookingCountText.toString() : "No bookings by travel class");

		// 5. Booking count last 5 years
		Integer bookingCount = this.repository.findBookingCountLastFiveYears(customerId);
		dashboard.setBookingCountLastFiveYears(bookingCount != null ? bookingCount : 0);

		// 6. Booking statistics (avg, min, max, stddev)
		List<Object[]> bookingStats = this.repository.findBookingStatisticsLastFiveYears(customerId);
		if (bookingStats != null)
			for (Object[] row : bookingStats)
				if (row != null && row.length >= 5) {
					String currency = row[0] != null ? (String) row[0] : "EUR";
					Money avgCost = new Money();
					Money minCost = new Money();
					Money maxCost = new Money();
					Money stdDevCost = new Money();
					avgCost.setCurrency(currency);
					minCost.setCurrency(currency);
					maxCost.setCurrency(currency);
					stdDevCost.setCurrency(currency);
					avgCost.setAmount((Double) row[1]);
					minCost.setAmount((Double) row[2]);
					maxCost.setAmount((Double) row[3]);
					stdDevCost.setAmount((Double) row[4]);

					dashboard.getBookingAverageCostLastFiveYears().add(avgCost);
					dashboard.getBookingMinCostLastFiveYears().add(minCost);
					dashboard.getBookingMaxCostLastFiveYears().add(maxCost);
					dashboard.getBookingStdDevCostLastFiveYears().add(stdDevCost);
				}

		// 7. Passenger statistics
		Integer passengerCount = this.repository.findPassengerCount(customerId);
		dashboard.setPassengerCount(passengerCount != null ? passengerCount : 0);

		List<Long> perBookingPassengerCounts = this.repository.findPassengerCountsPerBooking(customerId);
		if (perBookingPassengerCounts == null || perBookingPassengerCounts.isEmpty()) {
			dashboard.setPassengerAverage(0.0);
			dashboard.setPassengerMin(0);
			dashboard.setPassengerMax(0);
			dashboard.setPassengerStdDev(0.0);
		} else {
			IntSummaryStatistics stats = perBookingPassengerCounts.stream().filter(Objects::nonNull).mapToInt(Long::intValue).summaryStatistics();

			dashboard.setPassengerAverage(Math.round(stats.getAverage() * 100.0) / 100.0);
			dashboard.setPassengerMin(stats.getMin());
			dashboard.setPassengerMax(stats.getMax());
			dashboard.setPassengerStdDev(Math.round(this.calculateStdDev(perBookingPassengerCounts, stats.getAverage()) * 100.0) / 100.0);
		}

		super.getBuffer().addData(dashboard);
	}

	private double calculateStdDev(final List<Long> values, final double mean) {
		if (values == null || values.isEmpty())
			return 0.0;
		double sumSquared = 0.0;
		for (Long val : values)
			if (val != null)
				sumSquared += Math.pow(val - mean, 2);
		return Math.sqrt(sumSquared / values.size());
	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "moneySpentLastYear", "bookingCountByTravelClass", "bookingCountLastFiveYears", "bookingAverageCostLastFiveYears", "bookingMinCostLastFiveYears", "bookingMaxCostLastFiveYears",
			"bookingStdDevCostLastFiveYears", "passengerCount", "passengerAverage", "passengerMin", "passengerMax", "passengerStdDev");

		super.getResponse().addData(dataset);
	}
}
