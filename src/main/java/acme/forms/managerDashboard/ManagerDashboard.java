
package acme.forms.managerDashboard;

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
	private Long				legOnTime;
	private Long				legDelayed;
	private Long				legCancelled;
	private Long				legLanded;
	private Long				legsOnTime;
	private Long				legsDelayed;
	private Long				legs;
	private Money				averageFlightCost;
	private Money				minFlightCost;
	private Money				maxFlightCost;
	private Money				standarDerivationFlightCost;

}
