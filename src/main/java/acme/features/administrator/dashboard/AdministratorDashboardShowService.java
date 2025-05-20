
package acme.features.administrator.dashboard;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.forms.AdministratorDashboard;

@GuiService
public class AdministratorDashboardShowService extends AbstractGuiService<Administrator, AdministratorDashboard> {

	@Autowired
	private AdministratorDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		AdministratorDashboard dashboard = new AdministratorDashboard();

		// 1. Airports by operational scope
		List<Object[]> airportData = this.repository.countAirportsByScope();
		StringBuilder airportText = new StringBuilder();
		if (airportData != null)
			for (Object[] row : airportData)
				if (row != null && row.length >= 2)
					airportText.append(row[0].toString()).append(": ").append(row[1].toString()).append("\n");
		dashboard.setAirportCountByScope(airportText.length() > 0 ? airportText.toString() : "No airport data");

		// 2. Airlines by type
		List<Object[]> airlineData = this.repository.countAirlinesByType();
		StringBuilder airlineText = new StringBuilder();
		if (airlineData != null)
			for (Object[] row : airlineData)
				if (row != null && row.length >= 2)
					airlineText.append(row[0].toString()).append(": ").append(row[1].toString()).append("\n");
		dashboard.setAirlineCountByType(airlineText.length() > 0 ? airlineText.toString() : "No airline data");

		// 3. Airlines with both email and phone
		Long airlinesWithContacts = this.repository.countAirlinesWithBothContacts();
		Long totalAirlines = this.repository.countTotalAirlines();
		if (totalAirlines != null && totalAirlines > 0)
			dashboard.setAirlineWithContactRatio(Math.round(airlinesWithContacts.doubleValue() / totalAirlines.doubleValue() * 100.0) / 100.0);
		else
			dashboard.setAirlineWithContactRatio(0.0);

		// 4. Aircraft status ratios
		Long activeAircrafts = this.repository.countActiveAircrafts();
		Long inactiveAircrafts = this.repository.countInactiveAircrafts();
		Long totalAircrafts = this.repository.countTotalAircrafts();

		if (totalAircrafts != null && totalAircrafts > 0) {
			dashboard.setActiveAircraftRatio(Math.round(activeAircrafts.doubleValue() / totalAircrafts.doubleValue() * 100.0) / 100.0);
			dashboard.setInactiveAircraftRatio(Math.round(inactiveAircrafts.doubleValue() / totalAircrafts.doubleValue() * 100.0) / 100.0);
		} else {
			dashboard.setActiveAircraftRatio(0.0);
			dashboard.setInactiveAircraftRatio(0.0);
		}

		// 5. High score reviews ratio
		Long highScoreReviews = this.repository.countHighScoreReviews();
		Long totalReviews = this.repository.countTotalReviews();
		if (totalReviews != null && totalReviews > 0)
			dashboard.setHighScoreReviewRatio(Math.round(highScoreReviews.doubleValue() / totalReviews.doubleValue() * 100.0) / 100.0);
		else
			dashboard.setHighScoreReviewRatio(0.0);

		// 6. Reviews statistics last 10 weeks
		Integer reviewCount = this.repository.countReviewsLast10Weeks();
		dashboard.setReviewCountLast10Weeks(reviewCount != null ? reviewCount : 0);

		List<Long> reviewsPerWeek = this.repository.countReviewsPerWeekLast10Weeks();
		if (reviewsPerWeek == null || reviewsPerWeek.isEmpty()) {
			dashboard.setReviewAverageLast10Weeks(0.0);
			dashboard.setReviewMinLast10Weeks(0);
			dashboard.setReviewMaxLast10Weeks(0);
			dashboard.setReviewStdDevLast10Weeks(0.0);
		} else {
			IntSummaryStatistics stats = reviewsPerWeek.stream().filter(Objects::nonNull).mapToInt(Long::intValue).summaryStatistics();

			dashboard.setReviewAverageLast10Weeks(Math.round(stats.getAverage() * 100.0) / 100.0);
			dashboard.setReviewMinLast10Weeks(stats.getMin());
			dashboard.setReviewMaxLast10Weeks(stats.getMax());

			double mean = stats.getAverage();
			double sumSquared = reviewsPerWeek.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
			double stdDev = Math.sqrt(sumSquared / reviewsPerWeek.size());

			dashboard.setReviewStdDevLast10Weeks(Math.round(stdDev * 100.0) / 100.0);
		}

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AdministratorDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "airportCountByScope", "airlineCountByType", "airlineWithContactRatio", "activeAircraftRatio", "inactiveAircraftRatio", "highScoreReviewRatio", "reviewCountLast10Weeks", "reviewAverageLast10Weeks",
			"reviewMinLast10Weeks", "reviewMaxLast10Weeks", "reviewStdDevLast10Weeks");

		super.getResponse().addData(dataset);
	}
}
