
package acme.features.assistanceAgent.claims;

import java.util.Arrays;
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
public class AssistanceAgentClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int id;
		Claim claim;
		AssistanceAgent assistanceAgent;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			id = super.getRequest().getData("id", int.class);
			claim = this.repository.findClaimById(id);
			assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
			status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && claim != null && claim.isDraftMode();
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
		Collection<Leg> legs;
		Collection<ClaimType> types;
		ClaimType type;
		int legId;
		Leg leg;
		int agentId;
		AssistanceAgent assistanceAgent;
		boolean isCorrectLeg = true;
		boolean isNullLeg = true;
		boolean isCorrectType;

		types = Arrays.asList(ClaimType.values());
		type = super.getRequest().getData("type", ClaimType.class);
		isCorrectType = types.contains(type);

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		legs = this.repository.findAllPublishedLegs(MomentHelper.getCurrentMoment(), assistanceAgent.getAirline().getId());

		if (legs.isEmpty())
			isNullLeg = false;
		else {
			legId = super.getRequest().getData("leg", int.class);
			leg = this.repository.findLegById(legId);
			isCorrectLeg = legs.contains(leg);
		}

		if (!isCorrectType)
			throw new IllegalStateException("It is not posible to update a claim with this type");
		if (!isCorrectLeg)
			throw new IllegalStateException("It is not posible to update a claim with this leg");
		super.state(isNullLeg, "leg", "acme.validation.claim.legNull.message");
		super.state(claim.isDraftMode(), "draftMode", "acme.validation.claim.draftMode.message");
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices types;
		Dataset dataset;
		int agentId;
		AssistanceAgent assistanceAgent;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		legs = this.repository.findAllPublishedLegs(MomentHelper.getCurrentMoment(), assistanceAgent.getAirline().getId());
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "draftMode");
		dataset.put("accepted", claim.getAccepted());

		if (legs.isEmpty()) {
			choices = new SelectChoices();
			dataset.put("leg", choices.getSelected() != null && choices.getSelected().getKey() != null ? choices.getSelected().getKey() : "0");
			dataset.put("legs", choices);
		} else {
			choices = SelectChoices.from(legs, "flightNumber", claim.getLeg());
			dataset.put("leg", choices.getSelected().getKey());
			dataset.put("legs", choices);
		}

		types = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("types", types);

		super.getResponse().addData(dataset);
	}

}
