
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.customer.Customer;
import acme.entities.flights.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Column(unique = true)   //Preguntar a esta gente
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Automapped
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				purchaseMoment;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private TravelClass			travelClass;

	@Mandatory
	@ValidNumber
	@Automapped
	private Double				price;

	@Optional
	@ValidString(pattern = "^\\d{4}$")
	@Automapped
	private String				lastCardNibble;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer			customer;

	@ManyToOne
	@JoinColumn(name = "flight_id")
	private Flight				flight;

}
