
package acme.realms.customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidPhoneNumber;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@ValidCustomer
public class Customer extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				identifier;

	@Mandatory
	@ValidPhoneNumber
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				address;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				city;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				country;

	@Optional
	@ValidNumber(min = 0, max = 500000)
	@Automapped
	private Integer				earnedPoints;


	@Transient
	public String getCustomIdentifier() {
		CustomerRepository repository;
		repository = SpringHelper.getBean(CustomerRepository.class);
		DefaultUserIdentity identity = this.getIdentity();

		if (identity == null || identity.getName() == null || identity.getSurname() == null)
			return null;

		String name = identity.getName().trim().toUpperCase();
		String surname = identity.getSurname().trim().toUpperCase();

		if (name.isEmpty() || surname.isEmpty())
			return null;

		String initials = "" + name.charAt(0) + surname.charAt(0);

		List<String> existingIdentifiers = repository.findAllIdentifiersStartingWith(initials);

		Set<String> existingSet = new HashSet<>(existingIdentifiers);

		for (int i = 0; i <= 999999; i++) {
			String numberPart = String.format("%06d", i);
			String candidate = initials + numberPart;

			if (!existingSet.contains(candidate))
				return candidate;
		}
		return null;
	}

}
