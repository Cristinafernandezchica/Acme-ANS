
package acme.features.any.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.service.Service;

@GuiService
public class ServiceImageService extends AbstractGuiService<Any, Service> {

	@Autowired
	private ServiceRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Service> pictures;

		pictures = this.repository.findAllService();

		super.getBuffer().addData(pictures);
	}

	@Override
	public void unbind(final Service picture) {
		Dataset dataset;

		dataset = super.unbindObject(picture, "name", "picture", "averageDwellTime", "promotionCode", "money");

		super.getResponse().addData(dataset);
	}

}
