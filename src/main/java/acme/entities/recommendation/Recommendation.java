
package acme.entities.recommendation;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.entities.airports.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Recommendation extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 100)
	@Automapped
	private String				title;

	@Mandatory
	@ValidString(min = 1, max = 500)
	@Automapped
	private String				description;

	@Mandatory
	@ValidMoment(past = false) // Fecha futura o presente para recomendaciones válidas
	@Automapped
	private Date				startDate;

	@Optional
	@ValidMoment(past = false)
	@Automapped
	private Date				endDate;

	@Mandatory
	@Automapped
	private Category			category;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				estimatedCost;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				link;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				airport;

	// Campos para almacenar datos de la API externa
	private String				externalId;
	private String				providerName;
}
