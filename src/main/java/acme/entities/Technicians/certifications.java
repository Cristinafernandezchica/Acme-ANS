
package acme.entities.Technicians;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class certifications extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Automapped
	private Technicians			technicians;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				certification;
}
