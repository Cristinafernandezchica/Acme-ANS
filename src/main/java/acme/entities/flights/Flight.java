
package acme.entities.flights;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;
import acme.realms.manager.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
@Table(indexes = {
	@Index(columnList = "draftMode"), @Index(columnList = "manager_id")
})
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 1, max = 50)
	@Mandatory(message = "May not be null")
	@Automapped
	private String				tag;

	@Valid
	@Mandatory
	@Automapped
	private Boolean				indication;

	@ValidMoney(min = 0.00, max = 1000000.00)
	@Mandatory(message = "May not be null")
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
		Date res = null;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());

		if (!wrapper.isEmpty()) {
			Leg earliestLeg = wrapper.get(0);
			for (Leg leg : wrapper)
				if (leg.getScheduledDeparture().before(earliestLeg.getScheduledDeparture()))
					earliestLeg = leg;
			res = earliestLeg.getScheduledDeparture();
		}

		return res;
	}

	@Transient
	public Date getScheduledArrival() {
		Date res = null;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());

		if (!wrapper.isEmpty()) {
			Leg latestLeg = wrapper.get(0);
			for (Leg leg : wrapper)
				if (leg.getScheduledArrival().after(latestLeg.getScheduledArrival()))
					latestLeg = leg;
			res = latestLeg.getScheduledArrival();
		}

		return res;
	}

	@Transient
	public String originCity() {
		String res = null;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());

		if (!wrapper.isEmpty()) {
			Leg earliestLeg = wrapper.get(0);
			for (Leg leg : wrapper)
				if (leg.getScheduledDeparture().before(earliestLeg.getScheduledDeparture()))
					earliestLeg = leg;
			res = earliestLeg.getDepartureAirport().getCity();
		}

		return res;
	}

	@Transient
	public String destinationCity() {
		String res = null;
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());

		if (!wrapper.isEmpty()) {
			Leg latestLeg = wrapper.get(0);
			for (Leg leg : wrapper)
				if (leg.getScheduledArrival().after(latestLeg.getScheduledArrival()))
					latestLeg = leg;
			res = latestLeg.getArrivalAirport().getCity();
		}

		return res;
	}

	@Transient
	public Integer layovers() {
		LegRepository repository;
		List<Leg> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightId(this.getId());

		return wrapper.size();
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
		String originAirport = firstLeg.getDepartureAirport().getIataCode();
		String destinationAirport = lastLeg.getArrivalAirport().getIataCode();

		return String.format("%s %s (%s) - %s %s (%s)", departureTime, originCity, originAirport, arrivalTime, destinationCity, destinationAirport);
	}

	// Relationships ------------------------------


	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Manager manager;

}
