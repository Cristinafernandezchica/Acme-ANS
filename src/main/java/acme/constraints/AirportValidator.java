
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.entities.airports.Airport;
import acme.entities.airports.AirportRepository;

public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	@Autowired
	private AirportRepository repository;


	@Override
	protected void initialise(final ValidAirport annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		Airport existing = this.repository.findByIataCode(airport.getIataCode());
		boolean isUnique = existing == null || existing.equals(airport);
		super.state(context, isUnique, "iataCode", "acme.validation.airport.iataCode.message");

		result = !super.hasErrors(context);
		return result;
	}

}
