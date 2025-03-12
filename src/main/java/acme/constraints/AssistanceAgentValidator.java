
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
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("An assistance agent can't be a null").addConstraintViolation();
		} else {
			DefaultUserIdentity identity = assistanceAgent.getIdentity();
			String employeeCode = assistanceAgent.getEmployeeCode();
			String name = identity.getName();
			String surname = identity.getSurname();

			char employeeCodeFirstChar = Character.toUpperCase(employeeCode.charAt(0));
			char employeeCodeSecondChar = Character.toUpperCase(employeeCode.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));

			String[] surnameParts = surname.split(" ");
			if (surnameParts.length > 1) {
				char surnameFirstChar = Character.toUpperCase(surnameParts[0].charAt(0));
				char surnameSecondChar = Character.toUpperCase(surnameParts[1].charAt(0));
				char employeeCodeThirdChar = Character.toUpperCase(employeeCode.charAt(2));

				if (employeeCodeFirstChar == nameFirstChar && employeeCodeSecondChar == surnameFirstChar && employeeCodeThirdChar == surnameSecondChar) {
					result = true;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("The employee code is incorrectly").addConstraintViolation();
				}
			} else {
				char surnameFirstChar = Character.toUpperCase(surname.charAt(0));

				if (employeeCodeFirstChar == nameFirstChar && employeeCodeSecondChar == surnameFirstChar) {
					result = true;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("The employee code is incorrectly").addConstraintViolation();
				}
			}
		}
		return result;
	}

}
