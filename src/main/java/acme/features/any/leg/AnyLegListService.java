
package acme.features.any.leg;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;

@GuiService
public class AnyLegListService extends AbstractGuiService<Any, Leg> {

	@Autowired
	private AnyLegRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		Collection<Leg> legs;
		Collection<Leg> orderedByMomentLegs;

		legs = this.repository.findAllPublishedLegsByFlightId(flightId);

		orderedByMomentLegs = legs.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());

		super.getResponse().addGlobal("flightId", flightId);
		super.getBuffer().addData(orderedByMomentLegs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());

		super.getResponse().addData(dataset);

	}

}
