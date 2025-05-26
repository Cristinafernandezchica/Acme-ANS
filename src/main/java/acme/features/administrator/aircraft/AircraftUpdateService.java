
package acme.features.administrator.aircraft;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.Status;
import acme.entities.airline.Airline;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;

@GuiService
public class AircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private AircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean auth;

		Aircraft aircraftSelected = null;
		boolean existingAircraft = false;
		boolean hasAtributes = false;
		boolean validAircraft = true;
		boolean validStatus = true;
		boolean invalidChange = true;

		String metodo = super.getRequest().getMethod();

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			Integer aircraftId = super.getRequest().getData("id", Integer.class);
			if (aircraftId != null) {
				List<Aircraft> allAircrafts = this.repository.findAllAircrafts();
				aircraftSelected = this.repository.findAircraftById(aircraftId);
				existingAircraft = aircraftSelected != null || allAircrafts.contains(aircraftSelected) && aircraftSelected != null;
				hasAtributes = super.getRequest().hasData("model");
			}
			if (metodo.equals("POST")) {
				Integer airlineId = super.getRequest().getData("airline", Integer.class);

				if (airlineId == null)
					validAircraft = false;
				else {
					Airline airline = this.repository.findAirlineById(airlineId);
					List<Airline> allAirlines = this.repository.findAllAirlines();
					if (airline == null && airlineId != 0 || !allAirlines.contains(airline) && airlineId != 0)
						validAircraft = false;
				}

				String status = super.getRequest().getData("status", String.class);
				if (status == null || status.trim().isEmpty() || Arrays.stream(Status.values()).noneMatch(tc -> tc.name().equals(status)) && !status.equals("0"))
					validStatus = false;

				String newModel = super.getRequest().getData("model", String.class);
				String newRegNum = super.getRequest().getData("registrationNumber", String.class);
				Integer newCapacity = super.getRequest().getData("numberPassengers", Integer.class);
				Integer newCargoW = super.getRequest().getData("cargoWeight", Integer.class);
				if (aircraftSelected != null
					&& (!aircraftSelected.getModel().equals(newModel) || !aircraftSelected.getRegistrationNumber().equals(newRegNum) || !aircraftSelected.getNumberPassengers().equals(newCapacity) || !aircraftSelected.getCargoWeight().equals(newCargoW)))
					invalidChange = false;

			}

		}
		auth = existingAircraft && hasAtributes && validStatus && validAircraft && invalidChange;
		super.getResponse().setAuthorised(auth);
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
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "status", "details", "airline");

	}

	@Override
	public void validate(final Aircraft aircraft) {

		boolean isFlying = false;
		List<Leg> legs = this.repository.findLegsByAircraft(aircraft.getId());
		if (!legs.isEmpty()) {
			for (Leg l : legs)
				if (l.getStatus().equals(LegStatus.ON_TIME) || l.getStatus().equals(LegStatus.DELAYED)) {
					Date departureTime = l.getScheduledDeparture();
					Date arrivalTime = l.getScheduledDeparture();
					isFlying = MomentHelper.isInRange(MomentHelper.getCurrentMoment(), departureTime, arrivalTime);
				}
			super.state(isFlying, "*", "acme.validation.legOnAir.message");
		}

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

		airlines = this.repository.findAllAirlines();
		selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		statuses = SelectChoices.from(Status.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "numberPassengers", "cargoWeight", "status", "details");
		dataset.put("airline", selectedAirlines.getSelected().getKey());
		dataset.put("airlines", selectedAirlines);
		dataset.put("statuses", statuses);

		super.getResponse().addData(dataset);

	}

}
