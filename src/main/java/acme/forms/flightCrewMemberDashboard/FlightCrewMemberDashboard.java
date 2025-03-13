
package acme.forms.flightCrewMemberDashboard;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------

	private static final long						serialVersionUID	= 1L;

	// Attributes -------------------------------------------

	private List<String>							lastDestinationsAssigned;
	private Integer									numberOfLegsLowIncident;
	private Integer									numberOfLegsMediumIncident;
	private Integer									numberOfLegsHighIncident;
	private List<FlightCrewMember>					crewMembersLastLeg;
	private Map<CurrentStatus, FlightAssignment>	flightAssignmentsByStatus;
	private Integer									averageDeviationNumberOfFlights;
	private Integer									minimumDeviationNumberOfFlights;
	private Integer									maximumDeviationNumberOfFlights;

}
