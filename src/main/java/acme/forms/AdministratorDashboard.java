
package acme.forms;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Airports
	private String				airportCountByScope;

	// Airlines
	private String				airlineCountByType;
	private Double				airlineWithContactRatio;

	// Aircrafts
	private Double				activeAircraftRatio;
	private Double				inactiveAircraftRatio;

	// Reviews
	private Double				highScoreReviewRatio;
	private Integer				reviewCountLast10Weeks;
	private Double				reviewAverageLast10Weeks;
	private Integer				reviewMinLast10Weeks;
	private Integer				reviewMaxLast10Weeks;
	private Double				reviewStdDevLast10Weeks;
}
