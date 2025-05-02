
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBooking;
import acme.entities.flights.Flight;
import acme.realms.customer.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidBooking
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Automapped
	private TravelClass			travelClass;

	@Mandatory
	@ValidMoney(min = 0, max = 10000000.00)
	@Automapped
	private Money				price;

	@Optional
	@ValidString(pattern = "^\\d{4}$")
	@Automapped
	private String				lastCardNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Customer			customer;

	@Optional
	@ManyToOne
	@Valid
	private Flight				flight;


	@Transient
	public Money getPrice() {
		BookingRepository repository;
		Double totalAmount;
		Money total = new Money();

		if (this.flight == null || this.flight.getCost() == null) {
			total.setAmount(0.0);
			total.setCurrency("EUR");
			return total;
		}

		repository = SpringHelper.getBean(BookingRepository.class);
		totalAmount = this.flight.getCost().getAmount() * repository.findPassengersByBookingId(this.getId()).size();

		total.setAmount(totalAmount);
		total.setCurrency(this.flight.getCost().getCurrency());

		return total;
	}

}
