
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Manager extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	@Mandatory
	private String				identifierNumber;

	@ValidNumber(min = 0)
	@Mandatory
	@Automapped
	private Integer				yearsExperience;

	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(past = true)
	@Mandatory
	@Automapped
	private Date				dateBirth;

	@ValidUrl
	@Optional
	@Automapped
	private String				picture;

	@ManyToOne(optional = false)
	@Mandatory
	private Airline				airline;
}
