
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLeg;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidLeg
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Mandatory
	@Column(unique = true)
	private String				flightNumber;

	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	private Date				scheduledDeparture;

	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	private Date				scheduledArrival;

	@Mandatory
	@Automapped
	private LegStatus			status;

	// Relationships ----------------------------------------

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airport				departureAirport;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airport				arrivalAirport;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Flight				flight;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Aircraft			aircraft;

	// Derived attributes ---------------------------


	@Transient
	private double getDuration() {
		return (double) (this.getScheduledArrival().getTime() - this.getScheduledDeparture().getTime()) * (1000 * 60 * 60);
	}

}
