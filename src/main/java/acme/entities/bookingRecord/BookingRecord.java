
package acme.entities.bookingRecord;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(indexes = {
	@Index(columnList = "booking_id, passenger_id"),
})
public class BookingRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne
	private Booking				booking;

	@Mandatory
	@Valid
	@ManyToOne
	private Passenger			passenger;
}
