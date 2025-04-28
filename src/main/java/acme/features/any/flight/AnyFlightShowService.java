
package acme.features.any.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;

@GuiService
public class AnyFlightShowService extends AbstractGuiService<Any, Flight> {

	@Autowired
	private AnyFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Flight flight;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);
		status = flight != null && !flight.isDraftMode();

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
	public void unbind(final Flight flight) {
		Dataset dataset;
		
		dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");
		dataset.put("originCity", flight.originCity());
		dataset.put("destinationCity", flight.destinationCity());
		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("layovers", flight.layovers());
		dataset.put("flightId", flight.getId());

		super.getResponse().addData(dataset);
	}

}
