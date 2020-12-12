package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ActivityOfferGetBookingMethodTest extends RollbackTestAbstractClass {
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
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);

		assertEquals(booking, this.offer.getBooking(booking.getReference()));
	}

	@Test
	public void successCancelled() {
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		booking.cancel();

		assertEquals(booking, this.offer.getBooking(booking.getCancel()));
	}

	@Test
	public void doesNotExist() {
		new Booking(this.provider, this.offer, NIF, IBAN);

		assertNull(this.offer.getBooking("XPTO"));
	}

}
