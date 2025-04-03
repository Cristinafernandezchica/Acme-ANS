
package acme.features.assistanceAgent.claims;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentCompletedClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		Collection<Claim> completedClaims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findAllClaimsByAssistanceAgentId(assistanceAgentId);
		completedClaims = claims.stream().filter(c -> c.getAccepted() != TrackingLogStatus.PENDING).collect(Collectors.toCollection(ArrayList::new));

		super.getBuffer().addData(completedClaims);

	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type");
		dataset.put("accepted", claim.getAccepted());
		dataset.put("leg", claim.getLeg().getFlightNumber());
		super.addPayload(dataset, claim, "description");

		super.getResponse().addData(dataset);

	}

}
