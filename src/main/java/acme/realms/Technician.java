
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Technician extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", min = 8, max = 9)
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", min = 6, max = 16)
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				specialisation;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				annualHealthTest;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer				yearsOfExperience;

	@Optional
	@ValidString(max = 255, min = 0)
	@Automapped
	private String				certifications;
}
