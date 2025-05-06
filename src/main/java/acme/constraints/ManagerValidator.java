
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.manager.Manager;
import acme.realms.manager.ManagerRepository;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Autowired
	private ManagerRepository repository;


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
			String identifierNumber = manager.getIdentifierNumber();

			Manager existingManager = this.repository.findManagerByIdentifierNumber(identifierNumber);
			if (existingManager != null && !existingManager.equals(manager))
				super.state(context, false, "identifierNumber", "acme.validation.manager.identifierNumber.not.unique");
		}
		return !super.hasErrors(context);
	}
}
