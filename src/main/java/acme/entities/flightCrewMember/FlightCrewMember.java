
package acme.entities.flightCrewMember;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightCrewMember {

	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	@Mandatory
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "\"^\\+?\\d{6,15}$")
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255)
	private String				languageSkills;

	@Mandatory
	private AvailabilityStatus	availabilityStatus;

	/*
	 * @Mandatory
	 * 
	 * @ManyToMany
	 * private Airline airline;
	 */

	@Mandatory
	@ValidMoney
	private Money				salary;

	@Optional
	@ValidNumber
	private Integer				yearsOfExperience;
}
