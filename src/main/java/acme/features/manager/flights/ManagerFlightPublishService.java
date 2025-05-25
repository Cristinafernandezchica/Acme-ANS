
package acme.features.manager.flights;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

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

		if (!super.getRequest().getData().isEmpty()) {
			Integer masterId = super.getRequest().getData("id", Integer.class);

			if (masterId != null) {
				Flight flight = this.repository.findFlightById(masterId);

				if (flight != null && flight.isDraftMode()) {
					Manager manager = flight.getManager();
					int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

					status = super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId() && super.getRequest().hasData("tag");
				}
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
			Set<String> acceptedCurrencies = Set.of("EUR", "USD", "GBP");
			boolean notAcceptedCurrency = acceptedCurrencies.contains(flight.getCost().getCurrency());
			super.state(notAcceptedCurrency, "cost", "acme.validation.manager.flights.currency.not.valid");
		}

		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Date currentDate = MomentHelper.getCurrentMoment();

		boolean hasLegs = !legs.isEmpty();
		boolean allPublishedLegs = legs.stream().allMatch(leg -> !leg.isDraftMode());
		boolean noPastLeg = legs.stream().noneMatch(leg -> MomentHelper.isBefore(leg.getScheduledDeparture(), currentDate));

		super.state(hasLegs, "*", "acme.validation.manager.flights.without.legs");
		super.state(allPublishedLegs, "*", "acme.validation.manager.flights.no.published.leg");
		super.state(noPastLeg, "*", "acme.validation.manager.flights.publish.past.leg");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");

		boolean hasLegs = !legs.isEmpty();
		dataset.put("originCity", hasLegs ? flight.originCity() : null);
		dataset.put("destinationCity", hasLegs ? flight.destinationCity() : null);
		dataset.put("scheduledDeparture", hasLegs ? flight.getScheduledDeparture() : null);
		dataset.put("scheduledArrival", hasLegs ? flight.getScheduledArrival() : null);
		dataset.put("layovers", hasLegs ? flight.layovers() : null);

		if (hasLegs)
			dataset.put("flightId", flight.getId());

		super.getResponse().addData(dataset);
	}

}
