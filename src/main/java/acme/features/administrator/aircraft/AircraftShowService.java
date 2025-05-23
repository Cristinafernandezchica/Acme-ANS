
package acme.features.administrator.aircraft;

import java.util.Collection;
import java.util.List;

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
public class AircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private AircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {

		Aircraft aircraftSelected;
		boolean existingAircraft = false;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			Integer aircraftId = super.getRequest().getData("id", Integer.class);
			if (aircraftId != null) {
				List<Aircraft> allAircrafts = this.repository.findAllAircrafts();
				aircraftSelected = this.repository.findAircraftById(aircraftId);
				existingAircraft = aircraftSelected != null || allAircrafts.contains(aircraftSelected);
			}

		}
		super.getResponse().setAuthorised(existingAircraft);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices statuses;
		Dataset dataset;
		SelectChoices selectedAirlines;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		statuses = SelectChoices.from(Status.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "numberPassengers", "cargoWeight", "status", "details");

		dataset.put("airlines", selectedAirlines);
		dataset.put("statuses", statuses);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);

	}

}
