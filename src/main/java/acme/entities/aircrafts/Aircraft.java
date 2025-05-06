
package acme.entities.aircrafts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
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

	@Mandatory(message = "Must not be null")
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				model;

	@Mandatory(message = "Must not be null")
	@Column(unique = true)
	@ValidString(min = 1, max = 50, message = "Its lenght must be between 1 and 50 characters")
	private String				registrationNumber;

	@Mandatory(message = "Must not be null")
	@Automapped
	@ValidNumber(min = 1, max = 255, message = "The number of passengers must be beetween 1 and 255")
	private Integer				numberPassengers;

	@Mandatory(message = "Must not be null")
	@ValidNumber(min = 2000, max = 50000, message = "The cargo weight must be beetween 2000 kg and 50000")
	@Automapped
	private Integer				cargoWeight;

	@Mandatory(message = "Must not be null")
	@Automapped
	private Status				status;

	@Optional
	@ValidString(min = 0, max = 255, message = "Its maximum lenght must be 255 characters")
	@Automapped
	private String				details;

	@Mandatory(message = "Must not be null")
	@ManyToOne(optional = false)
	@Valid
	private Airline				airline;


	@Transient
	public String getAircraftLabel() {
		return String.format("%s - %s - Passangers: %s", this.getRegistrationNumber(), this.getModel(), this.getNumberPassengers());
	}

}
