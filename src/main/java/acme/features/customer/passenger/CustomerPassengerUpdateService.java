
package acme.features.customer.passenger;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int passengerId;
		Integer masterId;
		Passenger passenger;
		Customer customer;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null)
				if (super.getRequest().hasData("passportNumber")) {

					passengerId = super.getRequest().getData("id", int.class);
					passenger = this.repository.findPassengerById(passengerId);
					customer = passenger == null ? null : passenger.getCustomer();
					status = customer != null && super.getRequest().getPrincipal().hasRealm(customer) && passenger.isDraftMode();
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
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");

	}

	@Override
	public void validate(final Passenger passenger) {
		int passengerId;
		boolean existsDuplicate;
		Date dateOfBirthValue;
		boolean isDateOfBirthPast;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passengerId = super.getRequest().getData("id", int.class);
		existsDuplicate = this.repository.existsAnotherPassengerWithSamePassport(passenger.getPassportNumber(), customer.getId(), passengerId);
		super.state(!existsDuplicate, "passportNumber", "acme.validation.passenger.duplicate-passport");

		dateOfBirthValue = super.getRequest().getData("dateOfBirth", Date.class);
		isDateOfBirthPast = dateOfBirthValue != null && dateOfBirthValue.before(MomentHelper.getCurrentMoment());
		super.state(isDateOfBirthPast, "dateOfBirth", "acme.validation.passenger.dateOfBirth.message");
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "draftMode");

		super.getResponse().addData(dataset);
	}
}
