
package acme.constraints;

import java.util.Objects;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.customer.Customer;
import acme.realms.customer.CustomerRepository;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Autowired
	private CustomerRepository repository;


	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;

		if (customer == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		// Check if identifier is null (even though @Mandatory catches this)
		if (customer.getIdentifier() == null)
			super.state(context, false, "identifier", "javax.validation.constraints.NotNull.message");
		else {
			Customer existing = this.repository.findByIdentifier(customer.getIdentifier());
			boolean isUnique = existing == null || Objects.equals(existing.getId(), customer.getId());
			super.state(context, isUnique, "identifier", "acme.validation.customer.identifier.message");
		}

		// Validate identity info: name and surname must be present
		DefaultUserIdentity identity = customer.getIdentity();
		if (identity == null || identity.getName() == null || identity.getSurname() == null || identity.getName().trim().isEmpty() || identity.getSurname().trim().isEmpty())
			super.state(context, false, "identifier", "javax.validation.constraints.NotNull.message");

		return !super.hasErrors(context);
	}
}
