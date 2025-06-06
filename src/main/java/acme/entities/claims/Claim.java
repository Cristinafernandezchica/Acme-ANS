
package acme.entities.claims;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import acme.realms.assistanceAgents.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "id"), @Index(columnList = "assistance_agent_id")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private ClaimType			type;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Leg					leg;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public TrackingLogStatus getAccepted() {
		TrackingLogStatus result;
		ClaimRepository repository;
		Collection<TrackingLog> trackingLogs;

		repository = SpringHelper.getBean(ClaimRepository.class);
		trackingLogs = repository.findTrackingLogsByClaimId(this.getId());

		if (trackingLogs.isEmpty())
			result = TrackingLogStatus.PENDING;
		else
			result = trackingLogs.stream().findFirst().map(t -> t.getStatus()).orElse(TrackingLogStatus.PENDING);
		return result;
	}

}
