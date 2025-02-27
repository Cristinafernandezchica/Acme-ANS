
package acme.entities.flights;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.legs.Leg;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(max = 50)
	@Mandatory
	@Automapped
	private String				tag;

	@Automapped
	@Mandatory
	private Boolean				selfTransfer;

	@ValidMoney(min = 0)
	@Mandatory
	@Automapped
	private Money				cost;

	@Optional
	@Automapped
	private String				description;

	// Probablemente dejar solo el ManyToOne de Leg
	@OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
	private List<Leg>			legs;

	@Mandatory
	@Automapped
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@Automapped
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory
	@ValidString
	private String				originCity;

	@Mandatory
	@ValidString
	private String				destinationCity;

	@Mandatory
	@ValidNumber(min = 0)
	private Integer				layovers;
}
