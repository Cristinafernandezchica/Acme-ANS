
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineShowService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int airlineId;
		Airline airline;
		boolean auth = true;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(airlineId);
		if (airline == null)
			auth = false;

		super.getResponse().setAuthorised(auth);
	}

	@Override
	public void load() {
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void validate(final Airline airline) {
		;
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices types;
		Dataset dataset;

		types = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		dataset.put("types", types);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);

	}
}
