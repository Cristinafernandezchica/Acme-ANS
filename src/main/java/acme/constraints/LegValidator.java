
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
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
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			// flightNumber have the iataCode
			String iataCodeFlightNumber = flightNumber.substring(0, 3);
			String airlineIataCode = leg.getFlight().getManager().getAirline().getIataCode();
			if (iataCodeFlightNumber.equals(airlineIataCode))
				result = true;

			// Doesn't overlap with another leg of the same flight
			List<Leg> flightsLegs = this.repository.findLegsByFlightId(leg.getFlight().getId());
			for (Leg l : flightsLegs) {
				if (leg.getId() == l.getId())
					continue;

				if (MomentHelper.isInRange(leg.getScheduledDeparture(), l.getScheduledDeparture(), l.getScheduledArrival())) {
					result = false;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("The leg can't be in the middle of another leg of the same flight").addConstraintViolation();
				}
			}

			// scheduledArrival >= scheduledDeparture + delta (1 min)
			Date departureWithDelta = MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 1, ChronoUnit.MINUTES);
			if (MomentHelper.isBefore(leg.getScheduledArrival(), departureWithDelta)) {
				result = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("The scheduled arrival can't be before the scheduled departure + 1 minute").addConstraintViolation();

			}

		}
		return result;
	}
}
