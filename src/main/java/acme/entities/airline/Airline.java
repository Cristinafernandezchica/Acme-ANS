
package acme.entities.airline;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAirportIATACode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "iataCode"), @Index(columnList = "id, iataCode")
})
public class Airline extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidAirportIATACode
	@Column(unique = true)
	private String				iataCode;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				website;

	@Mandatory
	@Automapped
	private AirlineType			type;

	@Mandatory
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				foundationMoment;

	@Optional
	@ValidEmail
	@Automapped
	private String				email;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

}
