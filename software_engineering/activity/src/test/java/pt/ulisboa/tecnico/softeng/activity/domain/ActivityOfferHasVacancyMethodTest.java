package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class ActivityOfferHasVacancyMethodTest extends RollbackTestAbstractClass {
	private static final String IBAN = "IBAN";
	private static final String NIF = "123456789";
	private ActivityProvider provider;
	private ActivityOffer offer;

	@Override
	public void populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		this.offer = new ActivityOffer(activity, begin, end, 30);
	}

	@Test
	public void success() {
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertTrue(this.offer.hasVacancy());
	}

	@Test
	public void bookingIsFull() {
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertFalse(this.offer.hasVacancy());
	}

	@Test
	public void bookingIsFullMinusOne() {
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertTrue(this.offer.hasVacancy());
	}

	@Test
	public void hasCancelledBookings(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);

		booking.cancel();

		Assert.assertTrue(this.offer.hasVacancy());
	}

	public void hasCancelledBookingsButFull(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);
		booking.cancel();
		booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);

		Assert.assertFalse(this.offer.hasVacancy());
	}

}
