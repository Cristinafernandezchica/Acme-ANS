
package acme.entities.involves;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.entities.maintenanceRecords.MaintenanceRecords;
import acme.entities.tasks.Tasks;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Involves extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	@Automapped
	private Tasks				tasks;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	@Automapped
	private MaintenanceRecords	maintenanceRecords;
}
