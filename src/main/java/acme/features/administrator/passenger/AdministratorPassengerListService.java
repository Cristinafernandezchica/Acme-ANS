
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@GuiService
public class AdministratorPassengerListService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty()) {
			bookingId = super.getRequest().getData("bookingId", int.class);
			booking = this.repository.findBookingById(bookingId);

			if (booking != null)
				if (super.getRequest().hasData("draftMode")) {
					boolean requestDraftMode = super.getRequest().getData("draftMode", boolean.class);
					boolean bookingDraftMode = booking.isDraftMode();

					if (requestDraftMode == bookingDraftMode)
						status = true;
				} else
					status = false; // No permitir acceso si no se proporciona draftMode
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int bookingId;

		if (super.getRequest().getData().isEmpty())
			passengers = List.of();
		else {
			bookingId = super.getRequest().getData("bookingId", int.class);
			passengers = this.repository.findPassengersByBookingId(bookingId);
			super.getResponse().addGlobal("bookingId", bookingId);
			if (super.getRequest().hasData("draftMode")) {
				boolean draftMode = super.getRequest().getData("draftMode", boolean.class);
				super.getResponse().addGlobal("draftMode", draftMode);
			}
		}
		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");

		super.getResponse().addData(dataset);
	}

}
