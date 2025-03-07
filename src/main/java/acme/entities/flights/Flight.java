
package acme.entities.flights;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;
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
	@ValidString(max = 255)
	private String				description;

	// Atributos derivados ---------------------------------


	@Transient
	private Date getScheduledDeparture() {
		Date res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlighId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).toList();
		res = orderedWrapper.getFirst().getScheduledDeparture();

		return res;
	}

	@Transient
	private Date getScheduledArrival() {
		Date res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlighId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledArrival)).toList();
		res = orderedWrapper.getLast().getScheduledArrival();

		return res;
	}

	@Transient
	private String originCity() {
		String res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlighId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).toList();
		res = orderedWrapper.getFirst().getDepartureAirport().getCity();

		return res;
	}

	@Transient
	private String destinationCity() {
		String res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlighId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledArrival)).toList();
		res = orderedWrapper.getLast().getArrivalAirport().getCity();

		return res;
	}

	@Transient
	private Integer layovers() {
		Integer res;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlighId(this.getId());
		res = wrapper.size();

		return res;

	}
}
