
package acme.constraints;

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
			boolean nextInpectionIsAfterMoment = mr.getInspectionDueDate().before(mr.getMoment());
			if (nextInpectionIsAfterMoment) {
				result = false;
				super.state(context, nextInpectionIsAfterMoment, "inspectionDueDate", "acme.validation.service.inspectionDueDate.message");
			}
		}
		return result;
	}

}
