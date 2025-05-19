
package acme.features.flightCrewMember.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.forms.flightCrewMemberDashboard.FlightCrewMemberDashboard;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	private FlightCrewMemberDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		FlightCrewMemberDashboard fcmDashboard = new FlightCrewMemberDashboard();

		List<String> lastDestinationsAssigned;
		Integer numberOfLegsLowIncident;
		Integer numberOfLegsMediumIncident;
		Integer numberOfLegsHighIncident;
		List<String> crewMembersLastLeg;
		Double averageDeviationNumberOfFlights;
		Integer minimumDeviationNumberOfFlights;
		Integer maximumDeviationNumberOfFlights;
		Double standardDeviationNumberOfFlights;

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();

		lastDestinationsAssigned = this.repository.findLastDestinations(fcmIdLogged);
		if (lastDestinationsAssigned.isEmpty())
			lastDestinationsAssigned = new ArrayList<>();
		else if (lastDestinationsAssigned.size() >= 5)
			lastDestinationsAssigned = lastDestinationsAssigned.subList(0, 5);

		numberOfLegsLowIncident = this.repository.findLegsCountBySeverityLevelsLow();
		numberOfLegsMediumIncident = this.repository.findLegsCountBySeverityLevelsMedium();
		numberOfLegsHighIncident = this.repository.findLegsCountBySeverityLevelsHigh();

		crewMembersLastLeg = this.repository.findCrewNamesInLastLeg(fcmIdLogged).stream().map(fcm -> fcm.getIdentity().getFullName().replace(",", "-")).toList();

		List<String> faConfirmed = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.CONFIRMED).stream().map(fa -> fa.getLegRelated().getLabel()).toList();
		List<String> faPending = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.CANCELLED).stream().map(fa -> fa.getLegRelated().getLabel()).toList();
		List<String> faDenied = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.PENDING).stream().map(fa -> fa.getLegRelated().getLabel()).toList();

		Date startDate = MomentHelper.getCurrentMoment();
		Date endDate = MomentHelper.getCurrentMoment();
		startDate.setMonth(startDate.getMonth() - 1);
		startDate.setDate(1);
		endDate.setMonth(startDate.getMonth() - 1);
		endDate.setDate(30);
		List<Long> counts = this.repository.getDailyAssignmentCounts(startDate, endDate, fcmIdLogged);
		minimumDeviationNumberOfFlights = counts.stream().min(Long::compare).orElse(0L).intValue();
		maximumDeviationNumberOfFlights = counts.stream().max(Long::compare).orElse(0L).intValue();
		averageDeviationNumberOfFlights = counts.stream().mapToLong(Long::longValue).average().orElse(0.0);
		standardDeviationNumberOfFlights = Math.sqrt(counts.stream().mapToDouble(c -> Math.pow(c - averageDeviationNumberOfFlights, 2)).average().orElse(0.0));

		fcmDashboard.setLastDestinationsAssigned(lastDestinationsAssigned);
		fcmDashboard.setNumberOfLegsLowIncident(numberOfLegsLowIncident);
		fcmDashboard.setNumberOfLegsMediumIncident(numberOfLegsMediumIncident);
		fcmDashboard.setNumberOfLegsHighIncident(numberOfLegsHighIncident);
		fcmDashboard.setCrewMembersLastLeg(crewMembersLastLeg);
		fcmDashboard.setConfirmedFlightAssignments(faConfirmed);
		fcmDashboard.setPendingFlightAssignments(faPending);
		fcmDashboard.setCancelledFlightAssignments(faDenied);
		fcmDashboard.setAverageDeviationNumberOfFlights(averageDeviationNumberOfFlights);
		fcmDashboard.setMinimumDeviationNumberOfFlights(minimumDeviationNumberOfFlights);
		fcmDashboard.setMaximumDeviationNumberOfFlights(maximumDeviationNumberOfFlights);
		fcmDashboard.setStandardDeviationNumberOfFlights(standardDeviationNumberOfFlights);

		super.getBuffer().addData(fcmDashboard);

	}

	@Override
	public void unbind(final FlightCrewMemberDashboard fcmDashboard) {

		Dataset dataset;
		dataset = super.unbindObject(fcmDashboard, "lastDestinationsAssigned", "numberOfLegsLowIncident", "numberOfLegsMediumIncident", "numberOfLegsHighIncident", "crewMembersLastLeg", "confirmedFlightAssignments", "pendingFlightAssignments",
			"cancelledFlightAssignments", "averageDeviationNumberOfFlights", "minimumDeviationNumberOfFlights", "maximumDeviationNumberOfFlights", "standardDeviationNumberOfFlights");

		super.getResponse().addData(dataset);

	}

}
