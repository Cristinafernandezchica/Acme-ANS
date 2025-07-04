
package acme.features.assistanceAgent.claims;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		Integer id;
		Claim claim;
		AssistanceAgent assistanceAgent;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
			id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				claim = this.repository.findClaimById(id);
				assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
				status = claim != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent) && assistanceAgentId == assistanceAgent.getId() && claim.isDraftMode();
			}
		}

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
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		super.bindObject(claim, "passengerEmail", "description", "type");
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);
		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices types;
		Dataset dataset;
		int agentId;
		AssistanceAgent assistanceAgent;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "draftMode");
		dataset.put("accepted", claim.getAccepted());

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		legs = this.repository.findAllPublishedLegs(MomentHelper.getCurrentMoment(), assistanceAgent.getAirline().getId());
		choices = SelectChoices.from(legs, "flightNumber", claim.getLeg());
		dataset.put("leg", choices.getSelected().getKey());
		dataset.put("legs", choices);

		types = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("types", types);

		super.getResponse().addData(dataset);
	}

}
