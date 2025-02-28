
package acme.entities.maintenanceRecords;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MaintenanceRecords extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Temporal(TemporalType.DATE)
	@ValidMoment(past = true)
	@Mandatory
	@Automapped
	private Date				moment;

	@Mandatory
	@Valid
	@Automapped
	private Status				status;

	@Temporal(TemporalType.DATE)
	@ValidMoment(past = false)
	@Mandatory
	@Automapped
	private Date				inspectionDueDate;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				estimatedCost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				notes;
}
