
package acme.entities.trackingLogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidTrackingLog;
import acme.entities.claims.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "claim_id", nullable = false)
	private Claim				claim;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				step;

	@Mandatory
	@ValidScore
	@Automapped
	private Double				resolutionPercentage;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				resolution;

	/*
	 * @Transient
	 * public TrackingLogStatus getStatus() {
	 * TrackingLogStatus indicator;
	 * if (Boolean.TRUE.equals(this.claim.getAccepted()))
	 * indicator = TrackingLogStatus.ACCEPTED;
	 * else if (Boolean.FALSE.equals(this.claim.getAccepted()))
	 * indicator = TrackingLogStatus.REJECTED;
	 * else
	 * indicator = TrackingLogStatus.PENDING;
	 * 
	 * return indicator;
	 * }
	 */


	@Transient
	public TrackingLogStatus getStatus() {
		TrackingLogStatus status;
		TrackingLogRepository repository;
		Boolean indicator;

		repository = SpringHelper.getBean(TrackingLogRepository.class);
		indicator = repository.findClaimIndicatorByTrackingLogId(this.getId());

		if (Boolean.TRUE.equals(indicator))
			status = TrackingLogStatus.ACCEPTED;
		else if (Boolean.FALSE.equals(indicator))
			status = TrackingLogStatus.REJECTED;
		else
			status = TrackingLogStatus.PENDING;
		return status;
	}

}
