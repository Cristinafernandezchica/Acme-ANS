
package acme.features.flightCrewMember.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.FlightAssignment;
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
		Map<CurrentStatus, List<FlightAssignment>> flightAssignmentsByStatus = new HashMap<>();
		Integer averageDeviationNumberOfFlights;
		Integer minimumDeviationNumberOfFlights;
		Integer maximumDeviationNumberOfFlights;
		Integer standardDeviationNumberOfFlights;

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();

		lastDestinationsAssigned = this.repository.findLastDestinations(fcmIdLogged);
		if (lastDestinationsAssigned.isEmpty())
			lastDestinationsAssigned = new ArrayList<>();
		else if (lastDestinationsAssigned.size() >= 5)
			lastDestinationsAssigned = lastDestinationsAssigned.subList(0, 5);

		List<Integer> incidentsClassified = this.repository.findLegsCountBySeverityLevels();
		// hacerlo por llamadas distintas por que si el resultado es 0 no pone 0, el length es 1
		numberOfLegsLowIncident = incidentsClassified.get(0);
		numberOfLegsMediumIncident = incidentsClassified.get(1);
		numberOfLegsHighIncident = incidentsClassified.get(2);

		crewMembersLastLeg = this.repository.findCrewNamesInLastLeg(fcmIdLogged).stream().map(fcm -> fcm.getIdentity().getFullName()).toList();

		List<String> faConfirmed = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.CONFIRMED).stream().map(fa -> fa.getLegRelated().getLabel()).toList();
		List<String> faPending = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.CANCELLED).stream().map(fa -> fa.getLegRelated().getLabel()).toList();
		List<String> faDenied = this.repository.findFlightAssignmentsByCrewMember(fcmIdLogged, CurrentStatus.PENDING).stream().map(fa -> fa.getLegRelated().getLabel()).toList();

		Date startDate = MomentHelper.getCurrentMoment();
		Date endDate = MomentHelper.getCurrentMoment();
		startDate.setMonth(startDate.getMonth() - 1);
		startDate.setDate(1);
		endDate.setMonth(startDate.getMonth() - 1);
		endDate.setDate(30);
		List<Integer> statsDeviationsFlights = this.repository.calculateFlightAssignmentStats(startDate, endDate, fcmIdLogged);
		averageDeviationNumberOfFlights = statsDeviationsFlights.get(0);
		minimumDeviationNumberOfFlights = statsDeviationsFlights.get(1);
		maximumDeviationNumberOfFlights = statsDeviationsFlights.get(2);
		standardDeviationNumberOfFlights = statsDeviationsFlights.get(3);

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
