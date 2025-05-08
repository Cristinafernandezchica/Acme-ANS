
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

	private static final long	serialVersionUID			= 1L;

	private List<String>		lastFiveDestinations;
	private String				moneySpentLastYearDisplay;
	private String				bookingCountByTravelClass;
	private Integer				bookingCountLastFiveYears;
	private String				bookingAverageCostLastFiveYearsDisplay;
	private String				bookingMinCostLastFiveYearsDisplay;
	private String				bookingMaxCostLastFiveYearsDisplay;
	private String				bookingStdDevCostLastFiveYearsDisplay;
	private Integer				passengerCount;
	private Double				passengerAverage;
	private Integer				passengerMin;
	private Integer				passengerMax;
	private Double				passengerStdDev;

	// Campos para almacenar los Money directamente
	private List<Money>			moneySpentLastYear			= new ArrayList<>();
	private List<Money>			bookingCostsLastFiveYears	= new ArrayList<>();


	public CustomerDashboard() {
		// Constructor por defecto
	}
}
