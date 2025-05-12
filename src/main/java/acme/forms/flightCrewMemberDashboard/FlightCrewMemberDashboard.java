
package acme.forms.flightCrewMemberDashboard;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------

	private List<String>		lastDestinationsAssigned;
	private Integer				numberOfLegsLowIncident;
	private Integer				numberOfLegsMediumIncident;
	private Integer				numberOfLegsHighIncident;
	private List<String>		crewMembersLastLeg;
	private List<String>		confirmedFlightAssignments;
	private List<String>		pendingFlightAssignments;
	private List<String>		cancelledFlightAssignments;
	private Integer				averageDeviationNumberOfFlights;
	private Integer				minimumDeviationNumberOfFlights;
	private Integer				maximumDeviationNumberOfFlights;
	private Integer				standardDeviationNumberOfFlights;

}
