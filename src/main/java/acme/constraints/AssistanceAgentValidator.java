
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.assistanceAgents.AssistanceAgent;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {

		boolean result = true;

		if (assistanceAgent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (assistanceAgent.getEmployeeCode() == null)
			super.state(context, false, "employeeCode", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity identity = assistanceAgent.getIdentity();
			String employeeCode = assistanceAgent.getEmployeeCode();
			String name = identity.getName();
			String surname = identity.getSurname();

			char employeeCodeFirstChar = Character.toUpperCase(employeeCode.charAt(0));
			char employeeCodeSecondChar = Character.toUpperCase(employeeCode.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));
			char surnameFirstChar = Character.toUpperCase(surname.charAt(0));

			if (!(employeeCodeFirstChar == nameFirstChar && employeeCodeSecondChar == surnameFirstChar))
				super.state(context, false, "employeeCode", "acme.validation.assistanceAgent.employeeCode");
		}
		result = !super.hasErrors(context);
		return result;
	}

}
