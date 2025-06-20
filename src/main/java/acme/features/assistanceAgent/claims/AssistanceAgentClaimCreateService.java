
package acme.features.assistanceAgent.claims;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

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
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		boolean isCorrectRole;
		boolean isCorrectType = true;
		boolean isCorrectLeg = true;
		boolean hasLegs = true;
		boolean isFakeUpdate = true;
		String type;
		Collection<Leg> legs;
		int legId;
		Leg leg;
		int agentId;
		AssistanceAgent assistanceAgent;

		isCorrectRole = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				isFakeUpdate = false;
		}

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assistanceAgent = this.repository.findAssistanceAgentById(agentId);
		legs = this.repository.findAllPublishedLegs(MomentHelper.getCurrentMoment(), assistanceAgent.getAirline().getId());

		if (legs.isEmpty())
			hasLegs = false;

		if (super.getRequest().getMethod().equals("POST")) {
			boolean hasLegParam = super.getRequest().getData().containsKey("leg");

			if (hasLegParam) {
				legId = super.getRequest().getData("leg", int.class);
				if (legId != 0) {
					leg = this.repository.findLegById(legId);
					if (!legs.contains(leg) || leg == null)
						isCorrectLeg = false;
				}
			}

			type = super.getRequest().getData("type", String.class);
			if (type != null && !Arrays.toString(ClaimType.values()).concat("0").contains(type))
				isCorrectType = false;

		}

		status = isCorrectRole && isFakeUpdate && hasLegs && isCorrectType && isCorrectLeg;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		AssistanceAgent assistanceAgent;
		Date moment;

		assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		moment = MomentHelper.getCurrentMoment();

		claim = new Claim();
		claim.setAssistanceAgent(assistanceAgent);
		claim.setDraftMode(true);
		claim.setRegistrationMoment(moment);

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
