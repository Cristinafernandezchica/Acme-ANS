
package acme.features.assistanceAgent.dashboard;

import java.time.Month;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	@Autowired
	private AssistanceAgentDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		AssistanceAgentDashboard assistanceAgentDashboard = new AssistanceAgentDashboard();

		Double ratioAcceptedClaims;
		Double ratioRejectedClaims;
		List<String> top3MonthsWithMostClaims;
		Double averageTrackingLogsPerClaim;
		Long maxTrackingLogsPerClaim;
		Long minTrackingLogsPerClaim;
		Double standardDeviationOfTrackingLogsPerClaim;
		Double averageClaimsPerAssistanceAgentForLastMonth;
		Long maxClaimsPerAssistanceAgentForLastMonth;
		Long minClaimsPerAssistanceAgentForLastMonth;
		Double standardDeviationClaimsPerAssistanceAgentForLastMonth;

		Collection<Claim> allClaims = this.repository.findAllClaimsByAssistanceAgentId(assistanceAgentId);

		Long acceptedClaims = allClaims.stream().filter(c -> c.getAccepted().equals(TrackingLogStatus.ACCEPTED)).count();

		ratioAcceptedClaims = allClaims.isEmpty() ? 0.0 : (double) acceptedClaims / allClaims.size();

		Long rejectedClaims = allClaims.stream().filter(c -> c.getAccepted().equals(TrackingLogStatus.REJECTED)).count();

		ratioRejectedClaims = allClaims.isEmpty() ? 0.0 : (double) rejectedClaims / allClaims.size();

		List<Object[]> monthsByClaimCount = this.repository.findTopMonthsByClaimCount(assistanceAgentId);

		List<Integer> top3MonthsWithMostClaimsInNumber = monthsByClaimCount.stream().limit(3).map(o -> (Integer) o[0]).toList();

		top3MonthsWithMostClaims = top3MonthsWithMostClaimsInNumber.stream().map(m -> Month.of(m).name()).toList();

		List<Long> counts = this.repository.findTrackingLogCountsByAssistanceAgent(assistanceAgentId);

		averageTrackingLogsPerClaim = counts.stream().mapToLong(Long::longValue).average().orElse(0.0);

		maxTrackingLogsPerClaim = counts.stream().mapToLong(Long::longValue).max().orElse(0L);

		minTrackingLogsPerClaim = counts.stream().mapToLong(Long::longValue).min().orElse(0L);

		if (counts.isEmpty())
			standardDeviationOfTrackingLogsPerClaim = 0.00;
		else {
			double variance = counts.stream().mapToDouble(c -> Math.pow(c - averageTrackingLogsPerClaim, 2)).sum() / counts.size();
			standardDeviationOfTrackingLogsPerClaim = Math.sqrt(variance);
		}

		List<Long> claimsForLastMonth = this.repository.findDailyClaimCountsByMonth(assistanceAgentId, 12, 2024);

		averageClaimsPerAssistanceAgentForLastMonth = claimsForLastMonth.stream().mapToLong(Long::longValue).average().orElse(0.0);

		maxClaimsPerAssistanceAgentForLastMonth = claimsForLastMonth.stream().mapToLong(Long::longValue).max().orElse(0L);

		minClaimsPerAssistanceAgentForLastMonth = claimsForLastMonth.stream().mapToLong(Long::longValue).min().orElse(0L);

		if (claimsForLastMonth.isEmpty())
			standardDeviationClaimsPerAssistanceAgentForLastMonth = 0.00;
		else {
			double variance = claimsForLastMonth.stream().mapToDouble(c -> Math.pow(c - averageClaimsPerAssistanceAgentForLastMonth, 2)).sum() / claimsForLastMonth.size();
			standardDeviationClaimsPerAssistanceAgentForLastMonth = Math.sqrt(variance);
		}

		assistanceAgentDashboard.setRatioAcceptedClaims(ratioAcceptedClaims);
		assistanceAgentDashboard.setRatioRejectedClaims(ratioRejectedClaims);
		assistanceAgentDashboard.setTop3MonthsWithMostClaims(top3MonthsWithMostClaims);
		assistanceAgentDashboard.setAverageTrackingLogsPerClaim(averageTrackingLogsPerClaim);
		assistanceAgentDashboard.setMaxTrackingLogsPerClaim(maxTrackingLogsPerClaim);
		assistanceAgentDashboard.setMinTrackingLogsPerClaim(minTrackingLogsPerClaim);
		assistanceAgentDashboard.setStandardDeviationOfTrackingLogsPerClaim(standardDeviationOfTrackingLogsPerClaim);
		assistanceAgentDashboard.setAverageClaimsPerAssistanceAgentForLastMonth(averageClaimsPerAssistanceAgentForLastMonth);
		assistanceAgentDashboard.setMaxClaimsPerAssistanceAgentForLastMonth(maxClaimsPerAssistanceAgentForLastMonth);
		assistanceAgentDashboard.setMinClaimsPerAssistanceAgentForLastMonth(minClaimsPerAssistanceAgentForLastMonth);
		assistanceAgentDashboard.setStandardDeviationClaimsPerAssistanceAgentForLastMonth(standardDeviationClaimsPerAssistanceAgentForLastMonth);

		super.getBuffer().addData(assistanceAgentDashboard);

	}

	@Override
	public void unbind(final AssistanceAgentDashboard assistanceAgentDashboard) {

		Dataset dataset;

		dataset = super.unbindObject(assistanceAgentDashboard, "ratioAcceptedClaims", "ratioRejectedClaims", "top3MonthsWithMostClaims", "averageTrackingLogsPerClaim", "maxTrackingLogsPerClaim", "minTrackingLogsPerClaim",
			"standardDeviationOfTrackingLogsPerClaim", "averageClaimsPerAssistanceAgentForLastMonth", "maxClaimsPerAssistanceAgentForLastMonth", "minClaimsPerAssistanceAgentForLastMonth", "standardDeviationClaimsPerAssistanceAgentForLastMonth");

		super.getResponse().addData(dataset);
	}

}
