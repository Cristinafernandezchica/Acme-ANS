
package acme.entities.systemCurrencies;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import acme.constraints.ValidSystemCurrencies;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidSystemCurrencies
public class SystemCurrencies extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidCurrency
	@Column(unique = true)
	private String				currency;

	@Mandatory
	@Automapped
	private boolean				systemCurrency;

}
