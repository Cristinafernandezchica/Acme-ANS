
package acme.features.authenticated.flightCrewMember;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMember.AvailabilityStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberCreateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMember object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new FlightCrewMember();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final FlightCrewMember object) {
		assert object != null;

		super.bindObject(object, "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		object.setEmployeeCode(this.getFlightCrewMemberIdentifier(object));

	}

	public String getFlightCrewMemberIdentifier(final FlightCrewMember object) {
		DefaultUserIdentity identity = object.getIdentity();

		if (identity == null || identity.getName() == null || identity.getSurname() == null)
			return null;

		String name = identity.getName().trim().toUpperCase();
		String surname = identity.getSurname().trim().toUpperCase();

		if (name.isEmpty() || surname.isEmpty())
			return null;

		String initials = "" + name.charAt(0) + surname.charAt(0);

		List<String> existingIdentifiers = this.repository.findAllIdentifiersStartingWith(initials);
		existingIdentifiers.remove(object.getEmployeeCode());

		Set<String> existingSet = new HashSet<>(existingIdentifiers);

		for (int i = 0; i <= 999999; i++) {
			String numberPart = String.format("%06d", i);
			String candidate = initials + numberPart;

			if (!existingSet.contains(candidate))
				return candidate;
		}
		return null;
	}

	@Override
	public void validate(final FlightCrewMember object) {
		DefaultUserIdentity identity = object.getIdentity();
		String identifierNumber = object.getEmployeeCode();
		String name = identity.getName();
		String surname = identity.getSurname();

		char identifierFirstChar = Character.toUpperCase(identifierNumber.charAt(0));
		char identifierSecondChar = Character.toUpperCase(identifierNumber.charAt(1));
		char nameFirstChar = Character.toUpperCase(name.charAt(0));
		char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
		boolean isIdentifierValid = identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar;
		super.state(isIdentifierValid, "employeeCode", "acme.validation.flightCrewMember.employeeCode");

		if (object.getSalary() != null) {
			boolean notAcceptedCurrency = object.getSalary().getCurrency().equals("EUR") || object.getSalary().getCurrency().equals("USD") || object.getSalary().getCurrency().equals("GBP");
			super.state(notAcceptedCurrency, "salary", "acme.validation.flightCrewMember.salary.not.valid");
		}

	}

	@Override
	public void perform(final FlightCrewMember object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final FlightCrewMember object) {
		Dataset dataset;

		SelectChoices availabilityStatuses;

		SelectChoices airlineChoices;
		List<Airline> airlines;

		availabilityStatuses = SelectChoices.from(AvailabilityStatus.class, object.getAvailabilityStatus());

		airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "name", object.getAirline());

		dataset = super.unbindObject(object, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		dataset.put("availabilityStatuses", availabilityStatuses);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("airlines", airlineChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
