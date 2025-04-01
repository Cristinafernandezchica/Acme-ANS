
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.flights.Flight;
import acme.entities.flights.FlightRepository;
import acme.entities.legs.Leg;

public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Autowired
	private FlightRepository repository;


	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {
		boolean res;

		if (flight == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {

			List<Leg> orderedLegs = this.repository.findLegsByFlightIdOrdered(flight.getId());
			if (orderedLegs != null)
				for (int i = 0; i < orderedLegs.size() - 1; ++i) {
					Leg currentLeg = orderedLegs.get(i);
					Leg nextLeg = orderedLegs.get(i + 1);
					if (MomentHelper.isAfter(currentLeg.getScheduledArrival(), nextLeg.getScheduledDeparture()))
						super.state(context, false, "*", "acme.validation.flight.legs.order");
				}
		}
		res = !super.hasErrors(context);

		return res;
	}

}
