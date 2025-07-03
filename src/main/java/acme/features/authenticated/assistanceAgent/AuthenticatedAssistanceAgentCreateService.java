
package acme.features.authenticated.assistanceAgent;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentCreateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAssistanceAgentRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent object;
		int userAccountId;
		UserAccount userAccount;
		Date moment;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);
		moment = MomentHelper.getCurrentMoment();

		object = new AssistanceAgent();
		object.setUserAccount(userAccount);
		object.setMoment(moment);

		super.getBuffer().addData(object);
	}

	public String getAssistanceAgentCode(final AssistanceAgent object) {
		DefaultUserIdentity identity = object.getIdentity();

		if (identity == null || identity.getName() == null || identity.getSurname() == null)
			return null;

		String name = identity.getName().trim().toUpperCase();
		String surname = identity.getSurname().trim().toUpperCase();

		if (name.isEmpty() || surname.isEmpty())
			return null;

		String initials = "" + name.charAt(0) + surname.charAt(0);

		List<String> existingIdentifiers = this.repository.findAllCodesStartingWith(initials);
		existingIdentifiers.remove(object.getEmployeeCode());

		Set<String> existingSet = new HashSet<>(existingIdentifiers);

		for (int i = 0; i <= 999999; i++) {
			String numberPart = String.format("%06d", i);
			String candidate = initials + numberPart;

			if (!existingSet.contains(candidate))
				return candidate;
		}
		return null;
	}

	@Override
	public void bind(final AssistanceAgent object) {
		assert object != null;

		super.bindObject(object, "spokenLanguages", "briefBio", "salary", "picture", "airline");
		object.setEmployeeCode(this.getAssistanceAgentCode(object));
	}

	@Override
	public void validate(final AssistanceAgent object) {
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

		dataset = super.unbindObject(object, "spokenLanguages", "briefBio", "salary", "picture");
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
