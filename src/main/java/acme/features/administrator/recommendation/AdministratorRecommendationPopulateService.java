
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
		boolean status = true;
		Integer airportId;
		Airport airport;
		Integer recommendationId;
		Recommendation recommendation;
		boolean existingRecom = true;

		if (super.getRequest().getMethod().equals("POST")) {
			recommendationId = super.getRequest().getData("id", Integer.class);
			recommendation = this.repository.findRecommedationById(recommendationId);
			existingRecom = recommendation == null;

			airportId = super.getRequest().getData("airport", Integer.class);
			if (airportId != null) {
				airport = this.airportRepository.findAirportById(airportId);
				if (airportId == 0)
					status &= true;
				else if (airport == null)
					status &= false;
			} else
				status &= false;

			String rawCategory = super.getRequest().getData("category", String.class);
			if (!Arrays.toString(Category.values()).concat("0").contains(rawCategory) || rawCategory == null || rawCategory == "")
				status = false;
		}
		super.getResponse().setAuthorised(status && existingRecom);
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

		recommendation.setAirport(airport);

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
