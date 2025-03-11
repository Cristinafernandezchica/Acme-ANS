
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------

	private Integer					rankingManager;
	private Integer					yearsToRetire;
	private Double					ratioOnTimeLegs;
	private Double					ratioDelayedLegs;
	private List<String>			mostPopularAirports;
	private List<String>			lessPopularAirports;
	private Map<String, Integer>	legsByStatus;
	private Money					averageFlightCost;
	private Money					minFlightCost;
	private Money					maxFlightCost;
	private Money					standarDerivationFLightCost;

}
