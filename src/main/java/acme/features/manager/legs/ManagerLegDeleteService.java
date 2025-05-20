
package acme.features.manager.legs;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int masterId;
		Leg leg;
		Manager manager;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			masterId = super.getRequest().getData("id", int.class);
			leg = this.repository.findLegById(masterId);
			boolean fN = super.getRequest().hasData("flightNumber");
			manager = leg == null ? null : leg.getFlight().getManager();
			status = leg != null && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId() && fN;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
	}

	@Override
	public void validate(final Leg leg) {
		boolean notPublished = leg.isDraftMode();
		super.state(notPublished, "*", "acme.validation.leg.published.delete");
		boolean flightNotPublished = leg.getFlight().isDraftMode();
		super.state(flightNotPublished, "*", "acme.validation.leg.published.flight.delete");
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.delete(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "draftMode");

		super.getResponse().addData(dataset);

	}

}
