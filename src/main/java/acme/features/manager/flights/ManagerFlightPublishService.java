
package acme.features.manager.flights;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Integer masterId;
		Flight flight;
		Manager manager;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty()) {
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				flight = this.repository.findFlightById(masterId);
				boolean tag = super.getRequest().hasData("tag");
				manager = flight == null ? null : flight.getManager();
				status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId() && tag;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		if (flight.getCost() != null) {
			boolean notAcceptedCurrency = flight.getCost().getCurrency().equals("EUR") || flight.getCost().getCurrency().equals("USD") || flight.getCost().getCurrency().equals("GBP");
			super.state(notAcceptedCurrency, "cost", "acme.validation.manager.flights.currency.not.valid");
		}

		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Date currentDate = MomentHelper.getCurrentMoment();

		boolean hasLegs = !legs.isEmpty();
		boolean anyDraftMode = !legs.stream().anyMatch(Leg::isDraftMode);
		boolean noPastLeg = true;
		for (Leg leg : legs)
			if (MomentHelper.isBefore(leg.getScheduledDeparture(), currentDate))
				noPastLeg = false;

		super.state(hasLegs, "*", "acme.validation.manager.flights.without.legs");
		super.state(anyDraftMode, "*", "acme.validation.manager.flights.no.published.leg");
		super.state(noPastLeg, "*", "acme.validation.manager.flights.publish.past.leg");

	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");
		if (!legs.isEmpty()) {
			dataset.put("originCity", flight.originCity());
			dataset.put("destinationCity", flight.destinationCity());
			dataset.put("scheduledDeparture", flight.getScheduledDeparture());
			dataset.put("scheduledArrival", flight.getScheduledArrival());
			dataset.put("layovers", flight.layovers());
			dataset.put("flightId", flight.getId());
		} else {
			dataset.put("originCity", null);
			dataset.put("destinationCity", null);
			dataset.put("scheduledDeparture", null);
			dataset.put("scheduledArrival", null);
			dataset.put("layovers", null);
		}
		super.getResponse().addData(dataset);
	}

}
