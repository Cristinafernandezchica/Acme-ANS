
package acme.forms.managerDashboard;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------

	private Integer				rankingManager;
	private Integer				yearsToRetire;
	private double				ratioOnTimeDelayedLegs;
	private String				mostPopularAirport;
	private String				lessPopularAirport;
	private Map<String, Long>	legsByStatus;
	// en vez de un map, un long para cada estado existente
	private Long				legsOnTime;
	private Long				legsDelayed;
	private Long				legs;
	private Money				averageFlightCost;
	private Money				minFlightCost;
	private Money				maxFlightCost;
	private Money				standarDerivationFlightCost;

}
