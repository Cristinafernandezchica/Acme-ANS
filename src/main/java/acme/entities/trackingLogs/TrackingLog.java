
package acme.entities.trackingLogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidTrackingLog;
import acme.entities.claims.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
@Table(indexes = {
	@Index(columnList = "claim_id, draftMode, resolutionPercentage"), @Index(columnList = "claim_id, draftMode"), @Index(columnList = "claim_id, resolutionPercentage"), @Index(columnList = "id, claim_id")
})
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim				claim;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	private Date				lastUpdateMoment;

	@Mandatory
	@Automapped
	@ValidString(min = 1, max = 50)
	private String				step;

	@Mandatory
	@Automapped
	@ValidScore
	private Double				resolutionPercentage;

	@Mandatory
	@Automapped
	@Valid
	private TrackingLogStatus	status;

	@Optional
	@Automapped
	@ValidString(max = 255)
	private String				resolution;

	@Mandatory
	@Automapped
	private boolean				draftMode;
}
