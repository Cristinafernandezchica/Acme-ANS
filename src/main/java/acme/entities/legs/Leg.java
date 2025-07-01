
package acme.entities.legs;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(indexes = {
	@Index(columnList = "flight_id, scheduledDeparture"), @Index(columnList = "flight_id"), @Index(columnList = "flight_id, draftMode"), @Index(columnList = "draftMode, scheduledArrival, aircraft_id"), @Index(columnList = "flight_id, arrival_airport_id"),
	@Index(columnList = "status, flight_id"), @Index(columnList = "draftMode"), @Index(columnList = "id")
})
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory(message = "May not be null")
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory(message = "May not be null")
	@ValidMoment(message = "Invalid date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory(message = "May not be null")
	@ValidMoment(message = "Invalid date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory(message = "May not be null")
	@Automapped
	private LegStatus			status;

	@Mandatory
	@Automapped
	private boolean				draftMode;

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
	public double getDuration() {
		double res = 0;
		long durationInMillis = 0;
		if (this.getScheduledArrival() != null && this.getScheduledDeparture() != null)
			durationInMillis = this.getScheduledArrival().getTime() - this.getScheduledDeparture().getTime();
		res = (double) durationInMillis / (1000 * 60 * 60);
		return res;
	}

	@Transient
	public String getLabel() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");

		String departureCity = this.departureAirport.getCity();
		String departureCountry = this.departureAirport.getCountry();
		String arrivalCity = this.arrivalAirport.getCity();
		String arrivalCountry = this.arrivalAirport.getCountry();
		String departureTime = timeFormat.format(this.getScheduledDeparture());
		String arrivalTime = timeFormat.format(this.getScheduledArrival());

		return String.format("%s: %s - %s: %s %s - %s", departureCountry, departureCity, arrivalCountry, arrivalCity, departureTime, arrivalTime);

	}

}
