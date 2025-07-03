
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	// Serialisation version --------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------

	private Double				ratioAcceptedClaims;

	private Double				ratioRejectedClaims;

	private List<String>		top3MonthsWithMostClaims;

	private Double				averageTrackingLogsPerClaim;
	private Long				maxTrackingLogsPerClaim;
	private Long				minTrackingLogsPerClaim;
	private Double				standardDeviationOfTrackingLogsPerClaim;

	private Double				averageClaimsPerAssistanceAgentForLastMonth;
	private Long				maxClaimsPerAssistanceAgentForLastMonth;
	private Long				minClaimsPerAssistanceAgentForLastMonth;
	private Double				standardDeviationClaimsPerAssistanceAgentForLastMonth;

}
