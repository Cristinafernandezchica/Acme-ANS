
package acme.entities.activitylog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flightAssignment.FlightAssignment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ActivityLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	private Date				registrationMoment;

	@Mandatory
	@ValidString(max = 50)
	private String				typeOfIncident;

	@Mandatory
	@ValidString(max = 255)
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	private Integer				severityLevel;

	// Relations -------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_assignment_id", nullable = false)
	private FlightAssignment	flightAssignmentRelated;

}
