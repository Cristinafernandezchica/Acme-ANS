
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {

		boolean result = false;

		if (customer == null)
			super.state(context, false, "customer", "javax.validation.constraints.NotNull.message");
		else if (customer.getIdentifier() == null)
			super.state(context, false, "identifier", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity identity = customer.getIdentity();
			String identifier = customer.getIdentifier();
			String name = identity.getName();
			String surname = identity.getSurname();

			char identifierFirstChar = Character.toUpperCase(identifier.charAt(0));
			char identifierSecondChar = Character.toUpperCase(identifier.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));
			char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
			// identifier have the first character of the name and the first character of the surname in the first and second charactersValidation 
			if (identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar)
				result = true;
			else {
				result = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("The customer identifier is not valid").addConstraintViolation();
			}
		}
		return result;
	}

}
