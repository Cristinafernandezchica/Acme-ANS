
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
			if (leg.getFlightNumber().length() == 7) {
				String iataCodeFlightNumber = leg.getFlightNumber().substring(0, 3);
				String airlineIataCode = leg.getFlight().getManager().getAirline().getIataCode();

				if (!iataCodeFlightNumber.equals(airlineIataCode))
					super.state(context, false, "flightNumber", "acme.validation.leg.iataCode.flightNumber");
			}
			Leg existingLeg = this.repository.findLegByFlightNumber(leg.getFlightNumber());

			if (existingLeg != null && !existingLeg.equals(leg))
				super.state(context, false, "flightNumber", "acme.validation.leg.flightNumber.not.unique");

			// Doesn't overlap with another leg of the same flight  --> Se ha pasado al sservicio de publicación de Legs
			/*
			 * List<Leg> flightsLegs = this.repository.findLegsByFlightId(leg.getFlight().getId());
			 * for (Leg l : flightsLegs) {
			 * if (leg.getId() == l.getId())
			 * continue;
			 * 
			 * if (MomentHelper.isInRange(leg.getScheduledDeparture(), l.getScheduledDeparture(), l.getScheduledArrival())) {
			 * result = false;
			 * super.state(context, false, "scheduledDeparture/scheduledArrival", "{acme.validation.leg.overlap.message}");
			 * }
			 * }
			 */

			// scheduledArrival >= scheduledDeparture + delta (5 min) --> Añadida al crear o actualizar una leg
			/*
			 * Date departureWithDelta = MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 5, ChronoUnit.MINUTES);
			 * if (MomentHelper.isBefore(leg.getScheduledArrival(), departureWithDelta)) {
			 * result = false;
			 * super.state(context, false, "scheduledDeparture/scheduledArrival", "{acme.validation.leg.departure.arrival.difference.message}");
			 * 
			 * }
			 */

			// scheduledDeparture after scheduledArrival --> Se ha puesto al publicar una leg, al crearla y al actualizarla
			/*
			 * if (MomentHelper.isBefore(leg.getScheduledArrival(), leg.getScheduledDeparture())) {
			 * result = false;
			 * super.state(context, false, "scheduledDeparture", "{acme.validation.leg.departure.after.arrival.message}");
			 * }
			 */
			// aircraft operative --> se ha puesto al publicar
			/*
			 * if (leg.getAircraft().getStatus().equals(Status.UNDER_MAINTENANCE)) {
			 * result = false;
			 * super.state(context, false, "aircraft", "{acme.validation.leg.operative.aircraft.message}");
			 * }
			 */

		}
		return !super.hasErrors(context);
	}
}
