
package acme.features.administrator.recommendation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.StringHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.recommendation.Category;
import acme.entities.recommendation.Recommendation;
import acme.features.administrator.airport.AdministratorAirportRepository;
import acme.features.customer.recommendation.RecommendationApiService;

@GuiService
public class AdministratorRecommendationPopulateService extends AbstractGuiService<Administrator, Recommendation> {

	@Autowired
	private AdministratorRecommendationRepository	repository;

	@Autowired
	private AdministratorAirportRepository			airportRepository;

	@Autowired
	private RecommendationApiService				apiService;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Recommendation recommendation = new Recommendation();
		super.getBuffer().addData(recommendation);
	}

	@Override
	public void bind(final Recommendation recommendation) {
		int airportId;
		Airport airport;

		airportId = super.getRequest().getData("airport", int.class);
		airport = this.airportRepository.findAirportById(airportId);

		if (airport == null && airportId != 0)
			throw new IllegalStateException("Airport selected is not valid or do not exists");

		recommendation.setAirport(airport);

		String rawCategory = super.getRequest().getData("category", String.class);

		if (rawCategory != null && !rawCategory.trim().isEmpty() && !rawCategory.equals("0")) {
			boolean categoryValid = Arrays.stream(Category.values()).anyMatch(tc -> tc.name().equals(rawCategory));

			if (!categoryValid)
				throw new IllegalStateException("Category selected is not valid");
		}

		super.bindObject(recommendation, "title", "description", "startDate", "endDate", "category", "estimatedCost", "link");
	}

	@Override
	public void validate(final Recommendation recommendation) {
		boolean areDatesValid = true;
		boolean isCityValid = true;
		boolean notAcceptedCurrency = true;

		if (recommendation.getAirport() != null)
			isCityValid = !StringHelper.isBlank(recommendation.getAirport().getCity()) && this.airportRepository.existsByCity(recommendation.getAirport().getCity());
		super.state(isCityValid, "airport", "administrator.recommendation.error.cityNotFound");

		if (recommendation.getStartDate() != null && recommendation.getEndDate() != null)
			areDatesValid = recommendation.getEndDate().after(recommendation.getStartDate());
		super.state(areDatesValid, "endDate", "administrator.recommendation.error.invalidDates");

		if (recommendation.getEstimatedCost() != null)
			notAcceptedCurrency = recommendation.getEstimatedCost().getCurrency().equals("EUR") || recommendation.getEstimatedCost().getCurrency().equals("USD") || recommendation.getEstimatedCost().getCurrency().equals("GBP");
		super.state(notAcceptedCurrency, "estimatedCost", "administrator.recommendation.error.currency.not.valid");

	}

	@Override
	public void perform(final Recommendation recommendation) {
		this.repository.save(recommendation);

		List<Recommendation> apiRecommendations = this.apiService.fetchRecommendationsForAirport(recommendation.getAirport().getIataCode());
		apiRecommendations.forEach(this.repository::save);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Collection<Airport> airports;
		SelectChoices cityChoices;
		SelectChoices categoryChoices;
		Dataset dataset;

		// Obtener ciudades únicas de aeropuertos
		airports = this.airportRepository.findAllAirports();
		cityChoices = SelectChoices.from(airports, "airportLabel", recommendation.getAirport());
		categoryChoices = SelectChoices.from(Category.class, recommendation.getCategory());

		dataset = super.unbindObject(recommendation, "title", "description", "startDate", "endDate", "category", "estimatedCost", "link"); //, "draftMode");
		dataset.put("cityChoices", cityChoices);
		dataset.put("airport", cityChoices.getSelected() != null && cityChoices.getSelected().getKey() != null ? cityChoices.getSelected().getKey() : "0");
		dataset.put("categoryChoices", categoryChoices);
		dataset.put("category", categoryChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
