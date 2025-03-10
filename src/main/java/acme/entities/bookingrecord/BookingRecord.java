
package acme.entities.bookingrecord;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookingRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne
	@JoinColumn(name = "booking_id")
	@Automapped
	private Booking				booking;

	@Mandatory
	@ManyToOne
	@JoinColumn(name = "passenger_id")
	@Automapped
	private Passenger			passenger;
}
