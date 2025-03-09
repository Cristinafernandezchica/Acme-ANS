
package acme.entities.flightAssignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	private FlightCrewsDuty		flightCrewsDuty;

	@Mandatory
	@ValidMoment(past = true)
	private Date				lastUpdate;

	@Mandatory
	private CurrentStatus		currentStatus;

	@Optional
	@ValidString(max = 255)
	private String				remarks;

	// Relations -------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_crew_member_id", nullable = false)
	private FlightCrewMember	flightCrewMemberAssigned;

}
