
package acme.features.administrator.recommendation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.recommendation.Recommendation;

@GuiController
public class AdministratorRecommendationController extends AbstractGuiController<Administrator, Recommendation> {

	@Autowired
	private AdministratorRecommendationPopulateService populateService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("populate", "create", this.populateService);
	}
}
