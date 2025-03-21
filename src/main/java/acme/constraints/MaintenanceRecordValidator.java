
package acme.constraints;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;

import acme.client.components.validation.AbstractValidator;
import acme.entities.maintenanceRecords.MaintenanceRecords;

@Valid
public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecords> {

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecords mr, final ConstraintValidatorContext context) {
		boolean result = false;
		if (mr == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else {
			boolean nextInpectionIsAfterMoment = mr.getInspectionDueDate().after(mr.getMoment());
			super.state(context, nextInpectionIsAfterMoment, "inspectionDueDate", "acme.validation.service.inspectionDueDate.message");
			if (nextInpectionIsAfterMoment)
				result = true;
		}
		return result;
	}

}
