
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

		// 1. Last five destinations
		List<String> destinations = this.repository.findLastFiveDestinations(customerId);
		if (destinations != null && !destinations.isEmpty())
			dashboard.setLastFiveDestinations(destinations.stream().limit(5).collect(Collectors.toList()));
		else
			dashboard.setLastFiveDestinations(List.of("No destinations found"));

		// 2. Money spent last year
		List<Booking> bookingsMoney = this.repository.findBookingsForMoneySpentLastYear(customerId);
		Map<String, Double> moneyByCurrency = new HashMap<>();

		if (bookingsMoney != null)
			for (Booking booking : bookingsMoney) {
				Money price = booking.getPrice();
				if (price != null && price.getAmount() != null) {
					moneyByCurrency.merge(price.getCurrency(), price.getAmount(), Double::sum);
					dashboard.getMoneySpentLastYear().add(price);
				}
			}

		// Convert to display text
		StringBuilder moneySpentText = new StringBuilder();
		moneyByCurrency.forEach((currency, amount) -> moneySpentText.append(currency).append(": ").append(String.format("%.2f", amount)).append("\n"));
		dashboard.setMoneySpentLastYearDisplay(moneySpentText.length() > 0 ? moneySpentText.toString() : "No data available");

		// 3. Booking count by travel class
		List<Object[]> bookingData = this.repository.findBookingCountByTravelClass(customerId);
		StringBuilder bookingCountText = new StringBuilder();
		if (bookingData != null)
			for (Object[] row : bookingData)
				if (row != null && row.length >= 2)
					bookingCountText.append(row[0].toString()).append(": ").append(row[1].toString()).append("\n");
		dashboard.setBookingCountByTravelClass(bookingCountText.length() > 0 ? bookingCountText.toString() : "No bookings by travel class");

		// 4. Booking count last 5 years
		Integer bookingCount = this.repository.findBookingCountLastFiveYears(customerId);
		dashboard.setBookingCountLastFiveYears(bookingCount != null ? bookingCount : 0);

		// 5. Booking statistics (costs from last 5 years)
		List<Booking> bookingsStats = this.repository.findBookingsForStatisticsLastFiveYears(customerId);
		Map<String, List<Double>> amountsByCurrency = new HashMap<>();

		if (bookingsStats != null)
			for (Booking booking : bookingsStats) {
				Money price = booking.getPrice();
				if (price != null && price.getAmount() != null) {
					amountsByCurrency.computeIfAbsent(price.getCurrency(), k -> new ArrayList<>()).add(price.getAmount());
					dashboard.getBookingCostsLastFiveYears().add(price);
				}
			}

		// Calculate statistics
		StringBuilder avgText = new StringBuilder();
		StringBuilder minText = new StringBuilder();
		StringBuilder maxText = new StringBuilder();
		StringBuilder stdDevText = new StringBuilder();

		amountsByCurrency.forEach((currency, amounts) -> {
			DoubleSummaryStatistics stats = amounts.stream().mapToDouble(Double::doubleValue).summaryStatistics();
			double stdDev = this.calculateStdDev(amounts, stats.getAverage());

			avgText.append(currency).append(": ").append(String.format("%.2f", stats.getAverage())).append("\n");
			minText.append(currency).append(": ").append(String.format("%.2f", stats.getMin())).append("\n");
			maxText.append(currency).append(": ").append(String.format("%.2f", stats.getMax())).append("\n");
			stdDevText.append(currency).append(": ").append(String.format("%.2f", stdDev)).append("\n");
		});

		dashboard.setBookingAverageCostLastFiveYearsDisplay(avgText.length() > 0 ? avgText.toString() : "No data available");
		dashboard.setBookingMinCostLastFiveYearsDisplay(minText.length() > 0 ? minText.toString() : "No data available");
		dashboard.setBookingMaxCostLastFiveYearsDisplay(maxText.length() > 0 ? maxText.toString() : "No data available");
		dashboard.setBookingStdDevCostLastFiveYearsDisplay(stdDevText.length() > 0 ? stdDevText.toString() : "No data available");

		// 6. Passenger statistics
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
			dashboard.setPassengerStdDev(Math.round(this.calculateStdDev(perBookingPassengerCounts.stream().mapToDouble(Long::doubleValue).boxed().collect(Collectors.toList()), stats.getAverage()) * 100.0) / 100.0);
		}

		super.getBuffer().addData(dashboard);
	}

	private double calculateStdDev(final List<Double> values, final double mean) {
		if (values == null || values.isEmpty())
			return 0.0;
		double sumSquared = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
		return Math.sqrt(sumSquared / values.size());
	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "moneySpentLastYearDisplay", "bookingCountByTravelClass", "bookingCountLastFiveYears", "bookingAverageCostLastFiveYearsDisplay", "bookingMinCostLastFiveYearsDisplay",
			"bookingMaxCostLastFiveYearsDisplay", "bookingStdDevCostLastFiveYearsDisplay", "passengerCount", "passengerAverage", "passengerMin", "passengerMax", "passengerStdDev", "moneySpentLastYear", "bookingCostsLastFiveYears");

		super.getResponse().addData(dataset);
	}
}
