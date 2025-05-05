
package acme.forms;

import java.util.ArrayList;
import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long	serialVersionUID				= 1L;

	private List<String>		lastFiveDestinations;

	private List<String>		moneySpentLastYearDisplay;
	private String				bookingCountByTravelClass;

	private Integer				bookingCountLastFiveYears;

	private List<String>		bookingAverageCostLastFiveYearsDisplay;
	private List<String>		bookingMinCostLastFiveYearsDisplay;
	private List<String>		bookingMaxCostLastFiveYearsDisplay;
	private List<String>		bookingStdDevCostLastFiveYearsDisplay;

	private Integer				passengerCount;
	private Double				passengerAverage;
	private Integer				passengerMin;
	private Integer				passengerMax;
	private Double				passengerStdDev;

	// List<Money> for internal use if needed for Money <Currency, Amount>
	private List<Money>			moneySpentLastYear				= new ArrayList<>();
	private List<Money>			bookingAverageCostLastFiveYears	= new ArrayList<>();
	private List<Money>			bookingMinCostLastFiveYears		= new ArrayList<>();
	private List<Money>			bookingMaxCostLastFiveYears		= new ArrayList<>();
	private List<Money>			bookingStdDevCostLastFiveYears	= new ArrayList<>();


	// Constructor para valores por defecto
	public CustomerDashboard() {
		Money defaultMoney = new Money();
		defaultMoney.setCurrency("EUR");
		defaultMoney.setAmount(0.0);

		this.moneySpentLastYear.add(defaultMoney);
		this.bookingAverageCostLastFiveYears.add(defaultMoney);
		this.bookingMinCostLastFiveYears.add(defaultMoney);
		this.bookingMaxCostLastFiveYears.add(defaultMoney);
		this.bookingStdDevCostLastFiveYears.add(defaultMoney);
	}
}
