
package acme.entities.aircrafts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				model;

	@Mandatory
	@Column(unique = true)
	@ValidString(min = 1, max = 50)
	private String				registrationNumber;

	@Mandatory
	@Automapped
	@ValidNumber(min = 1, max = 255)
	private Integer				numberPassengers;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Integer				cargoWeight;

	@Mandatory
	@Automapped
	private Status				status;

	@Optional
	@ValidString(min = 0,max = 255)
	@Automapped
	private String				details;

	@Mandatory
	@ManyToOne(optional = false)
	@JoinColumn(name = "airline_id", nullable = false)
	@Valid
	private Airline				airline;

}
