
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		if (leg == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (leg.getFlightNumber() == null)
			super.state(context, false, "flightNumber", "javax.validation.constraints.NotNull.message");
		else {
			if (leg.getFlightNumber().length() == 7 && leg.getAircraft() != null) {
				String iataCodeFlightNumber = leg.getFlightNumber().substring(0, 3);
				String airlineIataCode = leg.getAircraft().getAirline().getIataCode();

				if (!iataCodeFlightNumber.equals(airlineIataCode))
					super.state(context, false, "flightNumber", "acme.validation.leg.iataCode.flightNumber");
			}
			Leg existingLeg = this.repository.findLegByFlightNumber(leg.getFlightNumber());

			if (existingLeg != null && !existingLeg.equals(leg))
				super.state(context, false, "flightNumber", "acme.validation.leg.flightNumber.not.unique");

		}
		return !super.hasErrors(context);
	}
}
