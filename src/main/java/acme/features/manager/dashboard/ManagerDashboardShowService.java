
package acme.features.manager.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.forms.managerDashboard.ManagerDashboard;
import acme.realms.Manager;

@GuiService
public class ManagerDashboardShowService extends AbstractGuiService<Manager, ManagerDashboard> {

	@Autowired
	private ManagerDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		ManagerDashboard managerDashboard = new ManagerDashboard();

		Integer rankingManager;
		Integer yearsToRetire;
		double ratioOnTimeDelayedLegs;
		String mostPopularAirport;
		String lessPopularAirport;
		Map<String, Long> legsByStatus;
		Money averageFlightCost = null;
		Money minFlightCost = null;
		Money maxFlightCost = null;
		Money standarDerivationFlightCost = null;

		Map<Airport, Integer> popularAirports = null;
		Collection<Leg> managerLegs = this.repository.findLegsByManagerId(managerId);

		Integer onTimeLegs = this.repository.findOnTimeLegs(managerId).orElse(0);
		Integer delayedLegs = this.repository.findDelayedLegs(managerId).orElse(0);

		rankingManager = this.repository.findManagerRanking(managerId).orElse(0);
		yearsToRetire = this.repository.findYearsToRetire(managerId).orElse(0);
		if (delayedLegs == 0)
			ratioOnTimeDelayedLegs = 0;
		else
			ratioOnTimeDelayedLegs = onTimeLegs / delayedLegs;

		for (Leg l : managerLegs) {
			Airport originAirport = l.getDepartureAirport();
			Airport destinationAirport = l.getArrivalAirport();

			popularAirports.put(originAirport, popularAirports.getOrDefault(originAirport, 0) + 1);
			popularAirports.put(destinationAirport, popularAirports.getOrDefault(destinationAirport, 0) + 1);
		}

		LinkedHashMap<Airport, Integer> sortedMap = popularAirports.entrySet().stream().sorted((a1, a2) -> a2.getValue().compareTo(a1.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		mostPopularAirport = sortedMap.entrySet().iterator().next().getKey().getName();
		lessPopularAirport = new ArrayList<>(sortedMap.keySet()).get(sortedMap.size() - 1).getName();

		legsByStatus = this.getLegsGroupedByStatus(managerId);

		averageFlightCost.setAmount(this.repository.findAverageFlightCost(managerId).orElse(0.));
		averageFlightCost.setCurrency("EUR");
		minFlightCost.setAmount(this.repository.findMinimumFlightCost(managerId).orElse(0.));
		minFlightCost.setCurrency("EUR");
		maxFlightCost.setAmount(this.repository.findMaximumFlightCost(managerId).orElse(0.));
		maxFlightCost.setCurrency("EUR");

		standarDerivationFlightCost.setAmount(this.repository.findStandardDeviationOfFlightCost(managerId).orElse(0.));
		standarDerivationFlightCost.setCurrency("EUR");

		managerDashboard.setRankingManager(rankingManager);
		managerDashboard.setYearsToRetire(yearsToRetire);
		managerDashboard.setRatioOnTimeDelayedLegs(ratioOnTimeDelayedLegs);
		managerDashboard.setMostPopularAirport(mostPopularAirport);
		managerDashboard.setLessPopularAirport(lessPopularAirport);
		managerDashboard.setLegsByStatus(legsByStatus);
		managerDashboard.setAverageFlightCost(averageFlightCost);
		managerDashboard.setMinFlightCost(minFlightCost);
		managerDashboard.setMaxFlightCost(maxFlightCost);
		managerDashboard.setStandarDerivationFlightCost(standarDerivationFlightCost);

		super.getBuffer().addData(managerDashboard);

	}

	public Map<String, Long> getLegsGroupedByStatus(final int managerId) {
		List<Object[]> results = this.repository.findLegsGroupedByStatus(managerId).orElse(List.of());

		Map<String, Long> statusMap = results.stream().collect(Collectors.toMap(result -> (String) result[0], result -> (Long) result[1]));

		return statusMap;
	}

	@Override
	public void unbind(final ManagerDashboard managerDashboard) {

		Dataset dataset;

		dataset = super.unbindObject(managerDashboard, "rankingManager", "yearsToRetire", "ratioOnTimeDelayedLegs", "mostPopularAirport", "lessPopularAirport", "legsByStatus", "averageFlightCost", "minFlightCost", "maxFlightCost",
			"standarDerivationFlightCost");

		super.getResponse().addData(dataset);
	}

}
