
package acme.forms.flightCrewMemberDashboard.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegsSevertityLevelStats {

	private Double	average;
	private Integer	minimum;
	private Integer	maximum;


	public LegsSevertityLevelStats(final Double average, final Integer minimum, final Integer maximum) {
		this.average = average;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public LegsSevertityLevelStats() {
	}

}
