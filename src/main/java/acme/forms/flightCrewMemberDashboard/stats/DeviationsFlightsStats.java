
package acme.forms.flightCrewMemberDashboard.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviationsFlightsStats {

	private Double	average;
	private Integer	minimum;
	private Integer	maximum;
	private Double	standardDeviation;


	public DeviationsFlightsStats(final Double average, final Integer minimum, final Integer maximum, final Double standardDeviation) {
		this.average = average;
		this.minimum = minimum;
		this.maximum = maximum;
		this.standardDeviation = standardDeviation;
	}

	public DeviationsFlightsStats() {
	}

}
