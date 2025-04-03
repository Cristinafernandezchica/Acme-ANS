
package acme.features.manager.legs;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightId = super.getRequest().getData("flightId", int.class);
		Manager manager = this.repository.findFlightById(flightId).getManager();
		boolean status = super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		Collection<Leg> legs;
		Collection<Leg> orderedByMomentLegs;

		legs = this.repository.findAllLegsByFlightId(flightId);

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
