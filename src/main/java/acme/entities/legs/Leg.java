
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
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
	@ValidString(pattern = "^[A-Z]{2}\\d{4}$")
	private String				flightNumber;

	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				scheduledDeparture;

	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				scheduledArrival;

	@ValidNumber
	@Mandatory
	@Automapped
	private Integer				duration;

	@Mandatory
	@Automapped
	private LegStatus			status;

	@Mandatory
	private Airport				departureAirport;

	@Mandatory
	private Airport				arrivalAirport;

	// Comentada hasta tener la clase Aircraft
	// @Mandatory
	// private Aircraft aircraft;

	// Relationships

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_id", nullable = false)
	private Flight				flight;

}
