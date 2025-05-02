
package acme.entities.bookingRecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.constraints.ValidBookingRecord;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidBookingRecord
public class BookingRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne
	@Valid
	private Booking				booking;

	@Mandatory
	@ManyToOne
	@Valid
	private Passenger			passenger;
}
