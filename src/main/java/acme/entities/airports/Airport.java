
package acme.entities.airports;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAirportIATACode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Airport extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 1, max = 50)
	@Mandatory
	@Automapped
	private String					name;

	@ValidAirportIATACode
	@Mandatory
	@Column(unique = true)
	private String					iataCode;

	@Mandatory
	@Automapped
	private OperationalScopeType	operationalScope;

	@ValidString(min = 1, max = 50)
	@Mandatory
	@Automapped
	private String					city;

	@ValidString(min = 1, max = 50)
	@Mandatory
	@Automapped
	private String					country;

	@ValidUrl
	@Optional
	@Automapped
	private String					website;

	@ValidEmail
	@Optional
	@Automapped
	private String					email;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String					phoneNumber;


	@Transient
	public String getAirportLabel() {
		return String.format("%s, %s (%s)", this.getCity(), this.getCountry(), this.getIataCode());

	}

}
