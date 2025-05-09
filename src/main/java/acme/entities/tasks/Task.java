
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.realms.Technician;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Task extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	@Automapped
	private Technician			technician;

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

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public String getTaskLabel() {
		String descripcionAcortada = "";
		if (this.getDescription().length() > 20)
			descripcionAcortada = this.getDescription().substring(0, 20);
		else
			descripcionAcortada = this.getDescription().substring(0, this.getDescription().length());

		return String.format("id: %s - type: %s - descripcion: %s", this.getId(), this.getType(), descripcionAcortada);
	}

}
