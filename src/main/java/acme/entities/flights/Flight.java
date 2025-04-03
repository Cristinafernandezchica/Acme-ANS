
package acme.entities.flights;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.airline.Airline;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 1, max = 50)
	@Mandatory
	@Automapped
	private String				tag;

	@Valid
	@Mandatory
	@Automapped
	private Boolean				indication;

	@ValidMoney(min = 0.00, max = 1000000.00)
	@Mandatory
	@Automapped
	private Money				cost;

	@ValidString
	@Optional
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Atributos derivados ---------------------------------


	@Transient
	public Date getScheduledDeparture() {
		Date res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());
		res = orderedWrapper.getFirst().getScheduledDeparture();

		return res;
	}

	@Transient
	public Date getScheduledArrival() {
		Date res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledArrival)).collect(Collectors.toList());
		res = orderedWrapper.getLast().getScheduledArrival();

		return res;
	}

	@Transient
	public String originCity() {
		String res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());
		res = orderedWrapper.getFirst().getDepartureAirport().getCity();

		return res;
	}

	@Transient
	public String destinationCity() {
		String res;
		LegRepository repository;
		List<Leg> wrapper;
		List<Leg> orderedWrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());
		orderedWrapper = wrapper.stream().sorted(Comparator.comparing(Leg::getScheduledArrival)).collect(Collectors.toList());
		res = orderedWrapper.getLast().getArrivalAirport().getCity();

		return res;
	}

	@Transient
	public Integer layovers() {
		Integer res;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());
		res = wrapper.size();

		return res;

	}

	@Transient
	public String getFlightLabel() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Leg> legs = repository.findLegsByFlightId(this.getId());

		if (legs.isEmpty())
			return "Unknown Flight";

		List<Leg> orderedLegs = legs.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());

		Leg firstLeg = orderedLegs.get(0);
		Leg lastLeg = orderedLegs.get(orderedLegs.size() - 1);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String departureTime = timeFormat.format(firstLeg.getScheduledDeparture());
		String arrivalTime = timeFormat.format(lastLeg.getScheduledArrival());
		String originCity = firstLeg.getDepartureAirport().getCity();
		String destinationCity = lastLeg.getArrivalAirport().getCity();

		return String.format("%s %s %s %s", departureTime, originCity, arrivalTime, destinationCity);
	}

	// Relationships ------------------------------


	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Manager	manager;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airline	airline;
}
