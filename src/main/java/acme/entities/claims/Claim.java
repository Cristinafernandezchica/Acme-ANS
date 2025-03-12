
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
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
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
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private ClaimType			type;

	@ManyToOne(optional = false)
	@JoinColumn(name = "leg_id", nullable = false)
	@Valid
	private Leg					leg;


	@Transient
	public Boolean getAccepted() {
		Boolean result;
		ClaimRepository repository;
		TrackingLog trackingLog;

		repository = SpringHelper.getBean(ClaimRepository.class);
		trackingLog = repository.findLastTrackingLogByClaimId(this.getId()).orElse(null);
		if (trackingLog == null)
			result = null;
		else {
			TrackingLogStatus status = trackingLog.getStatus();
			if (status.equals(TrackingLogStatus.ACCEPTED))
				result = true;
			else if (status.equals(TrackingLogStatus.REJECTED))
				result = false;
			else
				result = null;
		}
		return result;
	}

}
