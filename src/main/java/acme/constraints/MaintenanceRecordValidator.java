
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;

import acme.client.components.validation.AbstractValidator;
import acme.entities.maintenanceRecords.MaintenanceRecord;

@Valid
public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord mr, final ConstraintValidatorContext context) {
		boolean result = true;
		if (mr == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			List<String> aceptedCurrencys = List.of("USD", "EUR", "GBP");
			boolean haveAnAceptedCurrency = aceptedCurrencys.contains(mr.getEstimatedCost().getCurrency());
			if (!haveAnAceptedCurrency) {
				result = false;
				super.state(context, haveAnAceptedCurrency, "estimatedCost", "acme.validation.maintenanceRecords.costs.message");
			}

		}
		return result;
	}
}
