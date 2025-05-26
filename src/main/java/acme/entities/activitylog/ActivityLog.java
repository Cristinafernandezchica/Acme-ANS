
package acme.entities.activitylog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
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
@Table(indexes = {
	@Index(columnList = "flight_assignment_id")
})
public class ActivityLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory(message = "Must not be null")
	@ValidMoment(past = true, min = "2000/01/01 00:00", max = "2100/01/01 00:00", message = "Must be past")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory(message = "May not be null")
	@ValidString(min = 1, max = 50, message = "Must not be too long")
	@Automapped
	private String				typeOfIncident;

	@Mandatory(message = "May not be null")
	@ValidString(min = 1, max = 255, message = "Must not be too long")
	@Automapped
	private String				description;

	@Mandatory(message = "May not be null")
	@ValidNumber(min = 0, max = 10, message = "Must be beetween 0 and 10")
	@Automapped
	private Integer				severityLevel;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relations -------------------------------------------------------------

	@Mandatory(message = "May not be null")
	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_assignment_id", nullable = false)
	private FlightAssignment	flightAssignmentRelated;

}
