
package acme.entities.service;

import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Service extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidString
	@Automapped
	private String				picture;

	@Mandatory
	@ValidNumber(min = 0)
	@Automapped
	private Double				averageDwellTime;

	@Optional
	@ValidString(pattern = "^[A-Z]{4}-[0-9]{2}$")
	@Automapped
	private String				promotionCode;

	@Optional
	@ValidMoney
	@Automapped
	private Money				money;

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne(optional=false)
	 * private Airport airport;
	 */
}
