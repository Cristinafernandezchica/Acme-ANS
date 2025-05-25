
package acme.realms.manager;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
import acme.constraints.ValidManager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidManager
@Table(indexes = {
	@Index(columnList = "yearsExperience")
})
public class Manager extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Mandatory
	@Column(unique = true)
	private String				identifierNumber;

	@ValidNumber(min = 0, max = 120)
	@Mandatory
	@Automapped
	private Integer				yearsExperience;

	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dateBirth;

	@ValidUrl
	@Optional
	@Automapped
	private String				picture;

}
