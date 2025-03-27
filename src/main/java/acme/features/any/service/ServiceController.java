
package acme.features.any.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.service.Service;

@GuiController
public class ServiceController extends AbstractGuiController<Any, Service> {

	@Autowired
	private ServiceImageService imageService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.imageService);
	}
}
