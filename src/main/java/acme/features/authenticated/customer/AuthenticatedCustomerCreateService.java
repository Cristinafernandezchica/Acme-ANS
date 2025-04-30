/*
 * AuthenticatedCustomerCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.customer.Customer;

@GuiService
public class AuthenticatedCustomerCreateService extends AbstractGuiService<Authenticated, Customer> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Customer();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Customer object) {
		assert object != null;

		super.bindObject(object, "phoneNumber", "address", "city", "country");
		object.setEarnedPoints(0);
		object.setIdentifier(this.getCustomerIdentifier());

	}

	@Override
	public void validate(final Customer object) {
		DefaultUserIdentity identity = object.getIdentity();
		String identifier = object.getIdentifier();
		String name = identity.getName();
		String surname = identity.getSurname();

		char identifierFirstChar = Character.toUpperCase(identifier.charAt(0));
		char identifierSecondChar = Character.toUpperCase(identifier.charAt(1));
		char nameFirstChar = Character.toUpperCase(name.charAt(0));
		char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
		boolean isIdentifierValid = identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar;
		super.state(isIdentifierValid, "identifier", "acme.validation.customer.identifier.message");

	}

	@Override
	public void perform(final Customer object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Customer object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "identifier", "phoneNumber", "address", "city", "country", "earnedPoints");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

	private String getCustomerIdentifier() {
		UserAccount customer;
		int customerId;

		customerId = super.getRequest().getPrincipal().getAccountId();
		customer = this.repository.findUserAccountById(customerId);

		DefaultUserIdentity identity = customer.getIdentity();

		if (identity == null || identity.getName() == null || identity.getSurname() == null)
			return null;

		String name = identity.getName().trim().toUpperCase();
		String surname = identity.getSurname().trim().toUpperCase();

		if (name.isEmpty() || surname.isEmpty())
			return null;

		String initials = "" + name.charAt(0) + surname.charAt(0);

		List<String> existingIdentifiers = this.repository.findAllIdentifiersStartingWith(initials);

		Set<String> existingSet = new HashSet<>(existingIdentifiers);

		for (int i = 0; i <= 999999; i++) {
			String numberPart = String.format("%06d", i);
			String candidate = initials + numberPart;

			if (!existingSet.contains(candidate))
				return candidate;
		}
		return null;
	}

}
