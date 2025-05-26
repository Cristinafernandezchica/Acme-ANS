
package acme.features.administrator.aircraft;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.Status;
import acme.entities.airline.Airline;

@GuiService
public class AircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private AircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		Integer aircraftId;
		Aircraft aircraft;
		Airline validAirline;
		String aircraftStatus;
		boolean existingAircraft = true;

		if (super.getRequest().getMethod().equals("POST")) {
			aircraftId = super.getRequest().getData("id", Integer.class);
			aircraft = this.repository.findAircraftById(aircraftId);
			existingAircraft = aircraft == null;

			// Airline null
			Integer airlineId = super.getRequest().getData("airline", Integer.class);
			if (airlineId != null) {
				validAirline = this.repository.findAirlineById(airlineId);
				if (airlineId == 0)
					status &= true;
				else if (validAirline == null)
					status &= false;
			} else
				status &= false;

			// Invalid status

			aircraftStatus = super.getRequest().getData("status", String.class);
			if (!Arrays.toString(Status.values()).concat("0").contains(aircraftStatus) || aircraftStatus == null)
				status = false;

		}

		super.getResponse().setAuthorised(status && existingAircraft);

	}

	@Override
	public void load() {
		Aircraft aircraft;
		aircraft = new Aircraft();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("airline", int.class);
		airline = this.repository.findAirlineById(airlineId);

		super.bindObject(aircraft, "model", "registrationNumber", "numberPassengers", "cargoWeight", "status", "details");
		aircraft.setAirline(airline);
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statuses;
		SelectChoices selectedAirlines;
		Collection<Airline> airlines;

		statuses = SelectChoices.from(Status.class, aircraft.getStatus());
		airlines = this.repository.findAllAirlines();
		selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "numberPassengers", "cargoWeight", "status", "details");
		dataset.put("statuses", statuses);

		dataset.put("airlines", selectedAirlines);

		dataset.put("airline", selectedAirlines.getSelected().getKey());
		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
