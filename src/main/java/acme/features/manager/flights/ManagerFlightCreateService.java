
package acme.features.manager.flights;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Flight flight;
		Manager manager;

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		flight = new Flight();
		flight.setDraftMode(true);
		flight.setManager(manager);

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
		if (!flight.isDraftMode())
			super.state(false, "*", "acme.validation.flight.create.no.draftmode");

	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");

		super.getResponse().addData(dataset);
	}

}
