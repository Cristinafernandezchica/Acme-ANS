
package acme.features.assistanceAgent.claims;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentUndergoingClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> undergoingClaims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		undergoingClaims = this.repository.findAllUndergoingClaimsByAssistanceAgentId(assistanceAgentId);

		super.getBuffer().addData(undergoingClaims);

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
