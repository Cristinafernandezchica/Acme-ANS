
package acme.features.customer.recommendation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import acme.client.components.datatypes.Money;
import acme.entities.airports.Airport;
import acme.entities.recommendation.Category;
import acme.entities.recommendation.Recommendation;
import acme.entities.recommendation.RecommendationRepository;
import acme.features.administrator.airport.AdministratorAirportRepository;

@Service
public class RecommendationApiService {

	@Value("${api.recommendation.key}")
	private String							apiKey;

	@Autowired
	private AdministratorAirportRepository	airportRepository;

	@Autowired
	private RecommendationRepository		recommendationRepository;

	private static final String				FOURSQUARE_URL	= "https://api.foursquare.com/v3/places/search";


	public List<Recommendation> fetchRecommendationsForAirport(final String iataCode) {
		Airport airport = this.airportRepository.findByIataCode(iataCode).orElseThrow(() -> new IllegalArgumentException("Airport not found"));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", this.apiKey);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String query = String.format("near=%s,%s", airport.getCity(), airport.getCountry());
		String url = RecommendationApiService.FOURSQUARE_URL + "?" + query + "&limit=10";

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

			return this.parseApiResponse(response.getBody(), airport);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching recommendations", e);
		}
	}

	public List<Recommendation> fetchRecommendationsForCityAndCountry(final String city, final String country) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", this.apiKey);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String query = String.format("near=%s,%s", city, country);
		String url = RecommendationApiService.FOURSQUARE_URL + "?" + query + "&limit=5";

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

			return this.parseApiResponse(response.getBody(), city, country);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching recommendations", e);
		}
	}

	private List<Recommendation> parseApiResponse(final String json, final String city, final String country) {
		List<Recommendation> recommendations = new ArrayList<>();
		JsonNode root;

		try {
			root = new ObjectMapper().readTree(json);
			JsonNode results = root.path("results");

			for (JsonNode item : results) {
				Recommendation rec = new Recommendation();
				rec.setTitle(item.path("name").asText());
				rec.setDescription(item.path("location").path("address").asText());
				rec.setCategory(this.mapCategory(item.path("categories").get(0).path("name").asText()));
				rec.setEstimatedCost(this.estimateCost(item));
				rec.setLink("https://foursquare.com/v/" + item.path("fsq_id").asText());
				rec.setExternalId(item.path("fsq_id").asText());
				rec.setProviderName("Foursquare");

				recommendations.add(rec);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error parsing API response", e);
		}

		return recommendations;
	}

	private List<Recommendation> parseApiResponse(final String json, final Airport airport) {
		List<Recommendation> recommendations = new ArrayList<>();
		JsonNode root;

		try {
			root = new ObjectMapper().readTree(json);
			JsonNode results = root.path("results");

			for (JsonNode item : results) {
				Recommendation rec = new Recommendation();
				rec.setTitle(item.path("name").asText());
				rec.setDescription(item.path("location").path("address").asText());
				rec.setCategory(this.mapCategory(item.path("categories").get(0).path("name").asText()));
				rec.setEstimatedCost(this.estimateCost(item));
				rec.setLink("https://foursquare.com/v/" + item.path("fsq_id").asText());
				rec.setAirport(airport);
				rec.setExternalId(item.path("fsq_id").asText());
				rec.setProviderName("Foursquare");

				// Evitar duplicados
				if (!this.recommendationRepository.findByExternalIdAndProviderName(rec.getExternalId(), rec.getProviderName()).isPresent())
					recommendations.add(rec);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error parsing API response", e);
		}

		return recommendations;
	}

	private Category mapCategory(final String apiCategory) {
		// Lógica para mapear categorías de la API a tus categorías internas
		// Ejemplo simplificado:
		if (apiCategory.toLowerCase().contains("restaurant"))
			return Category.RESTAURANT;
		if (apiCategory.toLowerCase().contains("hotel"))
			return Category.ACCOMMODATION;
		return Category.ACTIVITY;
	}

	private Money estimateCost(final JsonNode item) {
		// Lógica para estimar costos basada en la categoría
		Money money = new Money();
		money.setCurrency("USD");
		// Ejemplo simplificado:
		switch (this.mapCategory(item.path("categories").get(0).path("name").asText())) {
		case RESTAURANT:
			money.setAmount(30.0);
			break;
		case ACCOMMODATION:
			money.setAmount(120.0);
			break;
		default:
			money.setAmount(15.0);
		}
		return money;
	}
}
