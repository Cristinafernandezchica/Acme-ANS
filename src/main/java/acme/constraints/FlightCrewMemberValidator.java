
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.realms.flightCrewMember.FlightCrewMember;

public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Override
	protected void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember flightCrewMember, final ConstraintValidatorContext context) {

		boolean result = false;

		if (flightCrewMember == null || flightCrewMember.getEmployeeCode() == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("An assistance agent can't be a null").addConstraintViolation();
		} else {
			DefaultUserIdentity identity = flightCrewMember.getIdentity();
			String identifierNumber = flightCrewMember.getEmployeeCode();
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
			context.buildConstraintViolationWithTemplate("The employee code is incorrectly").addConstraintViolation();
		}
		return result;
	}

}
