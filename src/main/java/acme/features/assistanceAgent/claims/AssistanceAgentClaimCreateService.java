
package acme.features.assistanceAgent.claims;

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
		super.getResponse().setAuthorised(true);
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
		claim.setAccepted(null);
		claim.setRegistrationMoment(moment);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		;
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
