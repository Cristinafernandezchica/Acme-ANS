
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
		boolean result = true;

		if (customer == null) {
			super.state(context, false, "customer", "javax.validation.constraints.NotNull.message");
			result = false;
		} else if (customer.getIdentifier() == null) {
			super.state(context, false, "identifier", "javax.validation.constraints.NotNull.message");
			result = false;
		} else {
			DefaultUserIdentity identity = customer.getIdentity();
			String identifier = customer.getIdentifier();
			String name = identity.getName();
			String surname = identity.getSurname();

			if (name == null || surname == null || name.isEmpty() || surname.isEmpty()) {
				super.state(context, false, "identifier", "The customer name, surname, and identifier must be properly defined");
				result = false;
			} else {
				// Check uniqueness at identifier attribute
				Customer existing = this.repository.findByIdentifier(identifier);
				if (existing != null && !Objects.equals(existing.getId(), customer.getId())) {
					super.state(context, false, "identifier", "The identifier must be unique");
					result = false;
				}
			}
		}
		return result;
	}

}
