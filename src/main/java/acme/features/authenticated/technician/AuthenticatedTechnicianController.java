
package acme.features.authenticated.technician;

import javax.annotation.PostConstruct;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.realms.Technician;

public class AuthenticatedTechnicianController extends AbstractGuiController<Authenticated, Technician> {

	@PostConstruct
	protected void initialise() {

	}
}
