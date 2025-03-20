
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.entities.flights.Flight;

public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight value, final ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
		return false;
	}

}
