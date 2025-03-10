
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgent;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {

		boolean result = false;

		if (assistanceAgent == null || assistanceAgent.getEmployeeCode() == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("An assistance agent can't be a null").addConstraintViolation();
		} else {
			DefaultUserIdentity identity = assistanceAgent.getIdentity();
			String identifierNumber = assistanceAgent.getEmployeeCode();
			String name = identity.getName();
			String surname = identity.getSurname();

			char identifierFirstChar = Character.toUpperCase(identifierNumber.charAt(0));
			char identifierSecondChar = Character.toUpperCase(identifierNumber.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));
			char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
			// identifierNumber have the first character of the name and the first character of the surname in the first and second charactersValidation 
			if (identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar)
				result = true;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("The emploee code is incorrectly").addConstraintViolation();
		}
		return result;
	}

}
