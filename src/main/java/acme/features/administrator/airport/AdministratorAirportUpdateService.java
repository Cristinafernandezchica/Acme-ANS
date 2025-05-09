
package acme.features.administrator.airport;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.airports.OperationalScopeType;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractService<Employer, Job> -------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		int id;
		Airport airport;
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			id = super.getRequest().getData("id", int.class);
			airport = this.repository.findAirportById(id);
			status = airport != null;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;
		int id;
		String iataCodeValue;
		boolean isIataCodeUnique;
		List<OperationalScopeType> operationalScopes;
		OperationalScopeType operationalScope;
		boolean isCorrectOperationalScope;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		iataCodeValue = super.getRequest().getData("iataCode", String.class);
		id = super.getRequest().getData("id", int.class);

		long count = this.repository.countByIataCodeExcludingAirport(iataCodeValue, id);

		isIataCodeUnique = count == 0;
		super.state(isIataCodeUnique, "iataCode", "acme.validation.airport.iataCode.message");

		operationalScopes = Arrays.asList(OperationalScopeType.values());
		operationalScope = super.getRequest().getData("operationalScope", OperationalScopeType.class);
		isCorrectOperationalScope = operationalScopes.contains(operationalScope);
		if (!isCorrectOperationalScope)
			throw new IllegalStateException("It is not posible to update an airport with this operational scope");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices operationalScopes;
		Dataset dataset;

		operationalScopes = SelectChoices.from(OperationalScopeType.class, airport.getOperationalScope());

		dataset = super.unbindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "email", "phoneNumber");
		dataset.put("operationalScopes", operationalScopes);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
