
package acme.features.administrator.bookingRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.bookingRecord.BookingRecord;

@GuiController
public class AdministratorBookingRecordController extends AbstractGuiController<Administrator, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingRecordListService	listService;

	@Autowired
	private AdministratorBookingRecordShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
