
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.technicians.Technicians;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Tasks extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	@Automapped
	private Technicians			technician;

	@Mandatory
	@Valid
	@Automapped
	private Type				type;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@ValidNumber(min = 0, max = 5000)
	@Automapped
	private Integer				estimatedDuration;
}
