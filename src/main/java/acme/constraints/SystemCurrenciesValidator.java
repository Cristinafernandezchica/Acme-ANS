
package acme.constraints;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.systemCurrencies.SystemCurrencies;
import acme.entities.systemCurrencies.SystemCurrenciesRepository;

@Validator
public class SystemCurrenciesValidator extends AbstractValidator<ValidSystemCurrencies, SystemCurrencies> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SystemCurrenciesRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidSystemCurrencies annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final SystemCurrencies currencyEntity, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		// 1. Currency uniqueness
		List<SystemCurrencies> allCurrencies = this.repository.findAllCurrencies();
		List<SystemCurrencies> duplicates = allCurrencies.stream().filter(c -> c.getCurrency().equals(currencyEntity.getCurrency()) && !c.equals(currencyEntity)).collect(Collectors.toList());

		boolean isUniqueCurrency = duplicates.isEmpty();
		super.state(context, isUniqueCurrency, "currency", "acme.validation.system-currencies.duplicated-currency");

		// 2. Only one currency can be the system currency
		long systemCurrencyCount = allCurrencies.stream().filter(SystemCurrencies::isSystemCurrency).filter(c -> !c.equals(currencyEntity)) // exclude itself (in case of update)
			.count();

		if (currencyEntity.isSystemCurrency())
			systemCurrencyCount++; // account for the current one if marked true

		boolean singleSystemCurrency = systemCurrencyCount == 1;
		super.state(context, singleSystemCurrency, "systemCurrency", "acme.validation.system-currencies.only-one-system-currency");

		result = !super.hasErrors(context);
		return result;
	}
}
