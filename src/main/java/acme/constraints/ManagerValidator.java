
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.manager.Manager;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {

		if (manager == null)
			super.state(context, false, "manager", "javax.validation.constraints.NotNull.message");
		else if (manager.getIdentifierNumber() == null)
			super.state(context, false, "identifierNumber", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity identity = manager.getIdentity();
			String identifierNumber = manager.getIdentifierNumber();
			String name = identity.getName();
			String surname = identity.getSurname();

			char identifierFirstChar = Character.toUpperCase(identifierNumber.charAt(0));
			char identifierSecondChar = Character.toUpperCase(identifierNumber.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));
			char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
			if (identifierFirstChar != nameFirstChar || identifierSecondChar != surnameFirstChar)
				super.state(context, false, "identifierNumber", "acme.validation.manager.identifierNumber");
		}
		return !super.hasErrors(context);
	}
}
