
package acme.entities.claims;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.passenger.Passenger;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "assistanceAgent_id", nullable = false)
	@Valid
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@Automapped
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@ManyToOne(optional = false)
	@JoinColumn(name = "passenger_id", nullable = false)
	@Valid
	private Passenger			passenger;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private ClaimType			type;

	@Optional
	@Valid
	@Automapped
	private Boolean				accepted;


	@Transient
	public String getPassengerEmail() {
		return this.getPassenger() != null ? this.getPassenger().getEmail() : null;
	}

}
