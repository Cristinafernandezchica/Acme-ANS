
package acme.entities.assistanceAgents;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssistanceAgent extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	private String				employeeCode;

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @Automapped
	 * 
	 * @OneToMany(mappedBy = "assistanceAgent", cascade = CascadeType.ALL, orphanRemoval = true)
	 * private List<Language> spokenLanguages;
	 */

	@ManyToOne(optional = false)
	@JoinColumn(name = "airline_id", nullable = false)
	private Airline				airline;

	@Mandatory
	@Automapped
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				employmentMoment;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				briefBio;

	@Optional
	@ValidMoney
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				photo;

}
