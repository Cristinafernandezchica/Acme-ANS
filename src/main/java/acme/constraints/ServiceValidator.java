
package acme.constraints;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.service.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		boolean result = false;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean promotionalCodeCorrect = false;
			Date fechaActual = new Date();
			SimpleDateFormat formato = new SimpleDateFormat("yy");
			String ultimosDosNumerosDelAño = formato.format(fechaActual);

			String ultimosDosDigitosDePromotionCode = service.getPromotionCode().substring(service.getPromotionCode().length() - 2);
			if (ultimosDosDigitosDePromotionCode.equals(ultimosDosNumerosDelAño))
				promotionalCodeCorrect = true;
			boolean moneyCorrect = false;
			if (service.getPromotionCode() != null)
				moneyCorrect = true;
			if (promotionalCodeCorrect && moneyCorrect)
				result = true;
		}
		return result;

	}

}
