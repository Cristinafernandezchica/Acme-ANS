
package acme.features.administrator.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;

@GuiService
public class AdministratorClaimShowService extends AbstractGuiService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		Integer claimId;
		Claim claim;

		if (!super.getRequest().getData().isEmpty()) {
			claimId = super.getRequest().getData("id", Integer.class);
			if (claimId != null) {
				claim = this.repository.findClaimById(claimId);
				status = claim != null && !claim.isDraftMode();
			}
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);

		this.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type");
		dataset.put("accepted", claim.getAccepted());
		dataset.put("leg", claim.getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}

}
