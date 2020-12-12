package pt.ulisboa.tecnico.softeng.hotel.services.local;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;

@RunWith(JMockit.class)
public class HotelInterfaceCancelBookingMethodTest extends RollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	private Hotel hotel;
	private Room room;
	private Booking booking;
	private final String NIF_BUYER = "123456789";
	private final String IBAN_BUYER = "IBAN_BUYER";

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0);
		this.room = new Room(this.hotel, "01", Type.DOUBLE);
		this.booking = this.room.reserve(Type.DOUBLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER);
	}

	@Test
	public void success() {
		String cancel = HotelInterface.cancelBooking(this.booking.getReference());

		Assert.assertTrue(this.booking.isCancelled());
		Assert.assertEquals(cancel, this.booking.getCancellation());
	}

	@Test(expected = HotelException.class)
	public void doesNotExist() {
		HotelInterface.cancelBooking("XPTO");
	}

	@Test(expected = HotelException.class)
	public void nullReference() {
		HotelInterface.cancelBooking(null);
	}

	@Test(expected = HotelException.class)
	public void emptyReference() {
		HotelInterface.cancelBooking("");
	}

	@Test
	public void successIntegration() {
		new Expectations() {
			{
				TaxInterface.cancelInvoice(this.anyString);
			}
		};
		String cancel = Hotel.cancelBooking(this.booking.getReference());

		Assert.assertTrue(this.booking.isCancelled());
		Assert.assertEquals(cancel, this.booking.getCancellation());
	}

	@Test(expected = HotelException.class)
	public void doesNotExistIntegration() {
		new Expectations() {
			{
				TaxInterface.cancelInvoice(this.anyString);
				this.times = 0;
			}
		};
		Hotel.cancelBooking("XPTO");
	}
}
