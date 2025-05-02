
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.airline.Airline;
import acme.features.administrator.airline.AdministratorAirlineRepository;

@Validator
public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (airline == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		// 1. Is IATA Code unique?

		Airline existing = this.repository.findByIATACode(airline.getIataCode());
		boolean isUnique = existing == null || existing.equals(airline);
		super.state(context, isUnique, "iataCode", "acme.validation.airline.iataCode.message");

		// 2. Is fundationMoment in a past date?

		Date now = MomentHelper.getCurrentMoment();
		boolean notFuture = airline.getFoundationMoment() != null && airline.getFoundationMoment().before(now);
		super.state(context, notFuture, "foundationMoment", "acme.validation.airline.foundationMoment.message");

		result = !super.hasErrors(context);
		return result;
	}
}
