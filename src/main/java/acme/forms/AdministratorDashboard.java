
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.airline.AirlineType;
import acme.entities.airports.OperationalScopeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	private static final long					serialVersionUID	= 1L;

	// Total number of airports grouped by their operational scope
	private Map<Integer, OperationalScopeType>	airportsByOperationalScope;

	// Number of airlines grouped by their type
	private Map<Integer, AirlineType>			airlinesByType;

	// Ratio of airlines with both an email address and a phone number
	private Double								airlinesWithContactRatio;

	// Ratios of active and non-active aircrafts
	private Double								activeAircraftRatio;
	private Double								nonActiveAircraftRatio;

	// Ratio of reviews with a score above 5.00
	private Double								highScoreReviewRatio;

	private Integer								reviewCountLastTenWeeks;
	private Double								reviewAvgLastTenWeeks;
	private Integer								reviewMinLastTenWeeks;
	private Integer								reviewMaxLastTenWeeks;
	private Double								reviewStdDevLastTenWeeks;
}
