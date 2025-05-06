
package acme.features.customer.dashboard;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
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
		if (dashboard.getMoneySpentLastYear() == null)
			dashboard.setMoneySpentLastYear(new ArrayList<>());
		if (dashboard.getBookingAverageCostLastFiveYears() == null)
			dashboard.setBookingAverageCostLastFiveYears(new ArrayList<>());
		if (dashboard.getBookingMinCostLastFiveYears() == null)
			dashboard.setBookingMinCostLastFiveYears(new ArrayList<>());
		if (dashboard.getBookingMaxCostLastFiveYears() == null)
			dashboard.setBookingMaxCostLastFiveYears(new ArrayList<>());
		if (dashboard.getBookingStdDevCostLastFiveYears() == null)
			dashboard.setBookingStdDevCostLastFiveYears(new ArrayList<>());

		// 2. Last five destinations
		List<String> destinations = this.repository.findLastFiveDestinations(customerId);
		if (destinations != null && !destinations.isEmpty())
			dashboard.setLastFiveDestinations(destinations.stream().limit(5).collect(Collectors.toList()));

		// 3. Money spent last year
		List<Booking> bookingsMoney = this.repository.findBookingsForMoneySpentLastYear(customerId);
		Map<String, Double> moneyByCurrency = new HashMap<>();

		if (bookingsMoney != null)
			for (Booking booking : bookingsMoney) {
				Money price = booking.getPrice(); // Esto usa el método transient
				if (price != null && price.getAmount() != null && price.getAmount() > 0)
					moneyByCurrency.merge(price.getCurrency(), price.getAmount(), Double::sum);
			}

		// Convertir a texto para mostrar
		StringBuilder moneySpentText = new StringBuilder();
		moneyByCurrency.forEach((currency, amount) -> moneySpentText.append(currency).append(": ").append(String.format("%.2f", amount)).append("\n"));
		dashboard.setMoneySpentLastYearDisplay(moneySpentText.length() > 0 ? moneySpentText.toString() : "No data available");

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
		List<Booking> bookingsStats = this.repository.findBookingsForStatisticsLastFiveYears(customerId);
		Map<String, List<Double>> amountsByCurrency = new HashMap<>();

		if (bookingsStats != null)
			for (Booking booking : bookingsStats) {
				Money price = booking.getPrice(); // Esto usa el método transient
				if (price != null && price.getAmount() != null && price.getAmount() > 0)
					amountsByCurrency.computeIfAbsent(price.getCurrency(), k -> new ArrayList<>()).add(price.getAmount());
			}

		// Calcular estadísticas
		StringBuilder avgText = new StringBuilder();
		StringBuilder minText = new StringBuilder();
		StringBuilder maxText = new StringBuilder();
		StringBuilder stdDevText = new StringBuilder();

		amountsByCurrency.forEach((currency, amounts) -> {
			DoubleSummaryStatistics stats = amounts.stream().mapToDouble(Double::doubleValue).summaryStatistics();

			double stdDev = this.calculateStdDevD(amounts, stats.getAverage());

			avgText.append(currency).append(": ").append(String.format("%.2f", stats.getAverage())).append("\n");
			minText.append(currency).append(": ").append(String.format("%.2f", stats.getMin())).append("\n");
			maxText.append(currency).append(": ").append(String.format("%.2f", stats.getMax())).append("\n");
			stdDevText.append(currency).append(": ").append(String.format("%.2f", stdDev)).append("\n");
		});

		dashboard.setBookingAverageCostLastFiveYearsDisplay(avgText.length() > 0 ? avgText.toString() : "No data available");
		dashboard.setBookingMinCostLastFiveYearsDisplay(minText.length() > 0 ? minText.toString() : "No data available");
		dashboard.setBookingMaxCostLastFiveYearsDisplay(maxText.length() > 0 ? maxText.toString() : "No data available");
		dashboard.setBookingStdDevCostLastFiveYearsDisplay(stdDevText.length() > 0 ? stdDevText.toString() : "No data available");

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
			dashboard.setPassengerStdDev(Math.round(this.calculateStdDevL(perBookingPassengerCounts, stats.getAverage()) * 100.0) / 100.0);
		}

		super.getBuffer().addData(dashboard);
	}

	private double calculateStdDevL(final List<Long> values, final double mean) {
		if (values == null || values.isEmpty())
			return 0.0;
		double sumSquared = 0.0;
		for (Long val : values)
			if (val != null)
				sumSquared += Math.pow(val - mean, 2);
		return Math.sqrt(sumSquared / values.size());
	}

	private double calculateStdDevD(final List<Double> values, final double mean) {
		if (values == null || values.isEmpty())
			return 0.0;
		double sumSquared = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
		return Math.sqrt(sumSquared / values.size());
	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "moneySpentLastYear", "bookingCountByTravelClass", "bookingCountLastFiveYears", "bookingAverageCostLastFiveYears", "bookingMinCostLastFiveYears", "bookingMaxCostLastFiveYears",
			"bookingStdDevCostLastFiveYears", "passengerCount", "passengerAverage", "passengerMin", "passengerMax", "passengerStdDev");

		super.getResponse().addData(dataset);
	}
}
