
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.booking.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long			serialVersionUID	= 1L;

	private List<String>				lastFiveDestinations;
	private Map<String, Double>			moneySpentLastYear;
	private Map<TravelClass, Integer>	bookingCountByTravelClass;

	private Integer						bookingCountLastFiveYears;
	private Map<String, Double>			bookingAverageCostLastFiveYears;
	private Map<String, Double>			bookingMinCostLastFiveYears;
	private Map<String, Double>			bookingMaxCostLastFiveYears;
	private Map<String, Double>			bookingStdDevCostLastFiveYears;

	private Integer						passengerCount;
	private Double						passengerAverage;	//Cuando haga la media, el double lo tengo que redondear
	private Integer						passengerMin;
	private Integer						passengerMax;
	private Double						passengerStdDev;	//Cuando haga la cuenta, el double lo tengo que redondear
}
