
package acme.features.administrator.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;

@GuiService
public class AdministratorPassengerShowService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Integer passengerId;
		Passenger passenger;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty()) {
			passengerId = super.getRequest().getData("id", Integer.class);
			if (passengerId != null) {
				passenger = this.repository.findPassengerById(passengerId);
				status = passenger != null && !passenger.isDraftMode() && this.repository.findPassengersWithBookingRecordAssigned().contains(passenger);
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "draftMode");

		super.getResponse().addData(dataset);
	}
}
