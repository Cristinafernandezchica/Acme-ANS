
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.booking.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long			serialVersionUID	= 1L;

	private List<String>				lastFiveDestinations;
	private Double						moneySpentLastYear;
	private Map<Integer, TravelClass>	bookingCountByTravelClass;

	private Money						bookingCountLastFiveYears;
	private Money						bookingAverageCostLastFiveYears;
	private Money						bookingMinCostLastFiveYears;
	private Money						bookingMaxCostLastFiveYears;
	private Money						bookingStdDevCostLastFiveYears;

	private Integer						passengerCount;
	private Integer						passengerAverage;	//Cuando haga la media, el double lo tengo que redondear
	private Integer						passengerMin;
	private Integer						passengerMax;
	private Integer						passengerStdDev;	//Cuando haga la cuenta, el double lo tengo que redondear
}
