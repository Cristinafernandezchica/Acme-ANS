
package acme.features.authenticated.assistanceAgent;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentUpdateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	@Autowired
	private AuthenticatedAssistanceAgentRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findAssistanceAgentByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final AssistanceAgent object) {
		Airline airline;
		assert object != null;
		Date moment;

		int id = super.getRequest().getData("id", int.class);
		AssistanceAgent previusAgent = this.repository.findAssistanceAgentById(id);
		airline = super.getRequest().getData("airline", Airline.class);
		moment = MomentHelper.getCurrentMoment();

		super.bindObject(object, "spokenLanguages", "briefBio", "salary", "picture", "airline");

		if (!airline.equals(previusAgent.getAirline()))
			object.setMoment(moment);

	}

	@Override
	public void validate(final AssistanceAgent object) {
		assert object != null;

		if (object.getSalary() != null) {
			boolean notAcceptedCurrency = object.getSalary().getCurrency().equals("EUR") || object.getSalary().getCurrency().equals("USD") || object.getSalary().getCurrency().equals("GBP");
			super.state(notAcceptedCurrency, "salary", "acme.validation.assistanceAgent.salary.not.valid");
		}
	}

	@Override
	public void perform(final AssistanceAgent object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {
		Dataset dataset;
		Collection<Airline> airlines;
		SelectChoices choices;

		AssistanceAgent assistanceAgent = this.repository.findAssistanceAgentById(object.getId());
		dataset = super.unbindObject(object, "spokenLanguages", "briefBio", "salary", "picture");
		dataset.put("employeeCode", assistanceAgent.getEmployeeCode());
		dataset.put("moment", assistanceAgent.getMoment());
		airlines = this.repository.findAllAirlines();
		choices = SelectChoices.from(airlines, "name", object.getAirline());
		dataset.put("airline", choices.getSelected().getKey());
		dataset.put("airlines", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
