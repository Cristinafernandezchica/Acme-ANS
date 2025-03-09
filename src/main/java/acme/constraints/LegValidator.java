
package acme.constraints;

import java.util.List;

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

		boolean result = false;
		String flightNumber = leg.getFlightNumber();

		if (flightNumber == null || !flightNumber.matches("^[A-Z]{2}X\\d{4}$"))
			result = false;
		else {
			String iataCodeFlightNumber = flightNumber.substring(0, 3);
			List<Leg> flightsLegs = this.repository.findLegsByFlighNumber(flightNumber);
			String airlineIataCode = flightsLegs.getFirst().getFlight().getManager().getAirline().getIataCode();
			if (iataCodeFlightNumber.equals(airlineIataCode))
				result = true;
		}
		return result;
	}
}
