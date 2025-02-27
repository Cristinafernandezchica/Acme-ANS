
package acme.entities.airlinemanagers;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;

public class AirlineManager extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	@Mandatory
	private String				identifierNumber;

	@ValidNumber
	@Mandatory
	@Automapped
	private Integer				yearsExperience;

	@Temporal(TemporalType.DATE)
	@ValidMoment(past = true)
	@Mandatory
	@Automapped
	private Date				dateBirth;

	@ValidUrl
	@Optional
	@Automapped
	private String				picture;
}
