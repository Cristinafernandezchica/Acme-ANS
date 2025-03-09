
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Column(unique = true)
	@Mandatory
	@ValidString(pattern = "^[A-Z]{2}X\\d{4}$")
	private String				flightNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				scheduledDeparture;

	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_id", nullable = false)
	private Flight				flight;

	// Comentada hasta tener la clase Aircraft
	// @Mandatory
	// private Aircraft aircraft;

	// Derived attributes ---------------------------


	@Transient
	private double getDuration() {
		return (double) (this.getScheduledArrival().getTime() - this.getScheduledDeparture().getTime()) * (1000 * 60 * 60);
	}

}
