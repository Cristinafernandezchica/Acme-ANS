
package acme.features.authenticated.flightCrewMember;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMember.AvailabilityStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberUpdateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMember object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findFCMByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final FlightCrewMember object) {
		assert object != null;

		FlightCrewMember flightCrewMember = this.repository.findFCMById(object.getId());

		super.bindObject(object, "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		object.setEmployeeCode(flightCrewMember.getEmployeeCode());
	}

	@Override
	public void validate(final FlightCrewMember object) {
		assert object != null;

	}

	@Override
	public void perform(final FlightCrewMember object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final FlightCrewMember object) {
		assert object != null;

		Dataset dataset;

		SelectChoices availabilityStatuses;

		SelectChoices airlineChoices;
		List<Airline> airlines;

		availabilityStatuses = SelectChoices.from(AvailabilityStatus.class, object.getAvailabilityStatus());

		airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "name", object.getAirline());

		FlightCrewMember FlightCrewMember = this.repository.findFCMById(object.getId());
		dataset = super.unbindObject(object, "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		dataset.put("employeeCode", FlightCrewMember.getEmployeeCode());
		dataset.put("availabilityStatuses", availabilityStatuses);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("airlines", airlineChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
