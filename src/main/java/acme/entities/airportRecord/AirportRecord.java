
package acme.entities.airportRecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.airline.Airline;
import acme.entities.airports.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AirportRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airport				airport;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airline				airline;
}
