
package acme.features.administrator.airline;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airport) {
		int airlineId;
		String iataCodeValue;
		boolean isIataCodeUnique;
		Date foundationMomentValue;
		boolean isfoundationMomentPast;
		boolean confirmation;

		iataCodeValue = super.getRequest().getData("iataCode", String.class);
		airlineId = super.getRequest().getData("id", int.class);

		int count = this.repository.countByIataCodeExcludingAirline(iataCodeValue, airlineId);

		isIataCodeUnique = count == 0;
		super.state(isIataCodeUnique, "iataCode", "acme.validation.airport.iataCode.message");

		foundationMomentValue = super.getRequest().getData("foundationMoment", Date.class);
		isfoundationMomentPast = foundationMomentValue.before(MomentHelper.getCurrentMoment());
		super.state(isfoundationMomentPast, "foundationMoment", "acme.validation.airline.foundationMoment.message");

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airport) {
		this.repository.save(airport);
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
