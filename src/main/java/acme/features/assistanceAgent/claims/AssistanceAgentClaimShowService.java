
package acme.features.assistanceAgent.claims;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Claim claim;
		AssistanceAgent assistanceAgent;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && claim != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices types;
		Dataset dataset;

		legs = this.repository.findAllPublishedLegs();
		choices = SelectChoices.from(legs, "flightNumber", claim.getLeg());
		types = SelectChoices.from(ClaimType.class, claim.getType());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "accepted", "draftMode");
		dataset.put("types", types);
		dataset.put("leg", choices.getSelected().getKey());
		dataset.put("legs", choices);

		super.getResponse().addData(dataset);
	}

}
