
package acme.features.manager.legs;

import java.util.Collection;
import java.util.Comparator;

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
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int managerId;
		Collection<Leg> legs;
		Collection<Leg> orderedByMomentLegs;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		legs = this.repository.findAllLegsByManagerId(managerId);

		orderedByMomentLegs = legs.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).toList();

		super.getBuffer().addData(orderedByMomentLegs);

	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");

		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());

		super.getResponse().addData(dataset);

	}

}
