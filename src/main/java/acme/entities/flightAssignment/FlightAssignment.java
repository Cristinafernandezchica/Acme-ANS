
package acme.entities.flightAssignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory(message = "Must not be null")
	@Automapped
	private FlightCrewsDuty		flightCrewsDuty;

	@Mandatory(message = "Must not be null")
	@ValidMoment(past = true, min = "2000/01/01 00:00", max = "2100/01/01 00:00", message = "Must be past")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdate;

	@Mandatory(message = "Must not be null")
	@Automapped
	private CurrentStatus		currentStatus;

	@Optional
	@ValidString(min = 0, max = 255, message = "Must not be too long")
	@Automapped
	private String				remarks;

	@Mandatory(message = "Must not be null")
	@Automapped
	private boolean				draftMode;

	// Relations -------------------------------------------------------------

	@Mandatory(message = "May not be null")
	@ManyToOne(optional = false)
	@Valid
	private FlightCrewMember	flightCrewMemberAssigned;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Leg					legRelated;

}
