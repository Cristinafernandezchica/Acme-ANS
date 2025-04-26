
package acme.features.authenticated.manager;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.features.authenticated.customer.AuthenticatedCustomerRepository;
import acme.realms.Manager;

@GuiService
public class AuthenticatedManagerCreateService extends AbstractGuiService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Manager object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Manager();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Manager object) {
		assert object != null;

		super.bindObject(object, "yearsExperience", "dateBirth", "picture");
		object.setIdentifierNumber(object.getManagerIdentifier());

	}

	@Override
	public void validate(final Manager object) {
		DefaultUserIdentity identity = object.getIdentity();
		String identifierNumber = object.getIdentifierNumber();
		String name = identity.getName();
		String surname = identity.getSurname();

		char identifierFirstChar = Character.toUpperCase(identifierNumber.charAt(0));
		char identifierSecondChar = Character.toUpperCase(identifierNumber.charAt(1));
		char nameFirstChar = Character.toUpperCase(name.charAt(0));
		char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
		boolean isIdentifierValid = identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar;
		super.state(isIdentifierValid, "identifierNumber", "acme.validation.manager.identifierNumber");
	}

	@Override
	public void perform(final Manager object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Manager object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "identifierNumber", "yearsExperience", "dateBirth", "picture");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
