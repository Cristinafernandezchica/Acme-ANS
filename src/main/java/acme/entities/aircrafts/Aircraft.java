
package acme.entities.aircrafts;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	@Mandatory
	@ValidString(max = 50)
	private String	model;

	@Mandatory
	@Column(unique = true)
	@ValidString(max = 50)
	private String	registrationNumber;

	@Mandatory
	private Integer	numberPassengers;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	private Integer	cargoWeight;

	@Mandatory
	private Status	status;

	@Optional
	@ValidString(max = 255)
	private String	details;

}
