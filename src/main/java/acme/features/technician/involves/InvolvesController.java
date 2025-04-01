
package acme.features.technician.involves;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.involves.Involves;
import acme.realms.Technician;

@GuiController
public class InvolvesController extends AbstractGuiController<Technician, Involves> {

	@Autowired
	private InvolvesListService	listService;

	@Autowired
	private InvolvesShowService	showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
