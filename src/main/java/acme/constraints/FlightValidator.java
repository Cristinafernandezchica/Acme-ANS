
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
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

		Integer layovers = flight.layovers();
		boolean res = true;

		if (layovers < 0 || layovers == 0) {
			res = false;
			super.state(context, false, "layovers", "{acme.validation.flight.layovers.zero.message}");
		}

		List<Leg> legs = this.repository.findAllLegsByFlightId(flight.getId());
		if (legs.isEmpty()) {
			res = false;
			super.state(context, false, "numberOfLegs", "{acme.validation.filght.legs.empty.message}");
		}

		return res;
	}

}
