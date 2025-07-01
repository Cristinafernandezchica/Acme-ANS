
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
	@Index(columnList = "assistance_agent_id")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	private Date				registrationMoment;

	@Mandatory
	@Automapped
	@ValidEmail
	private String				passengerEmail;

	@Mandatory
	@Automapped
	@ValidString(min = 1, max = 255)
	private String				description;

	@Mandatory
	@Automapped
	@Valid
	private ClaimType			type;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
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
