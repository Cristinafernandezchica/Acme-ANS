
package acme.realms.manager;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidManager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidManager
public class Manager extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Mandatory
	@Column(unique = true)
	private String				identifierNumber;

	@ValidNumber(min = 0, max = 120)
	@Mandatory
	@Automapped
	private Integer				yearsExperience;

	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dateBirth;

	@ValidUrl
	@Optional
	@Automapped
	private String				picture;


	@Transient
	public String getManagerIdentifier() {
		ManagerRepository repository;
		repository = SpringHelper.getBean(ManagerRepository.class);
		DefaultUserIdentity identity = this.getIdentity();

		if (identity == null || identity.getName() == null || identity.getSurname() == null)
			return null;

		String name = identity.getName().trim().toUpperCase();
		String surname = identity.getSurname().trim().toUpperCase();

		if (name.isEmpty() || surname.isEmpty())
			return null;

		String initials = "" + name.charAt(0) + surname.charAt(0);

		List<String> existingIdentifiers = repository.findAllIdentifiersStartingWith(initials);
		existingIdentifiers.remove(this.identifierNumber);

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
