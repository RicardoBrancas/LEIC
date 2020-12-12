package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;

@RunWith(JMockit.class)
public class BookingConstructorTest extends RollbackTestAbstractClass {
	private static final LocalDate ARRIVAL = new LocalDate(2016, 12, 19);
	private static final LocalDate DEPARTURE = new LocalDate(2016, 12, 21);
	private static final double ROOM_PRICE = 20.0;
	private static String NIF_BUYER = "123456789";
	private static String IBAN_BUYER = "IBAN_BUYER";
	private Room room;

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		Hotel hotel = new Hotel("XPTO123", "Londres", "NIF", "IBAN", 20.0, 30.0);
		this.room = new Room(hotel, "01", Room.Type.SINGLE);
	}

	@Test
	public void success() {
		Booking booking = new Booking(this.room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);

		Assert.assertTrue(booking.getReference().startsWith(this.room.getHotel().getCode()));
		Assert.assertTrue(booking.getReference().length() > Hotel.CODE_SIZE);
		Assert.assertEquals(ARRIVAL, booking.getArrival());
		Assert.assertEquals(DEPARTURE, booking.getDeparture());
		Assert.assertEquals(ROOM_PRICE * 2, booking.getPrice(), 0.0d);
	}

	@Test(expected = HotelException.class)
	public void nullRoom() {
		new Booking(null, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);
	}

	@Test(expected = HotelException.class)
	public void nullArrival() {
		new Booking(this.room, null, DEPARTURE, NIF_BUYER, IBAN_BUYER);
	}

	@Test(expected = HotelException.class)
	public void nullDeparture() {
		new Booking(this.room, ARRIVAL, null, NIF_BUYER, IBAN_BUYER);
	}

	@Test(expected = HotelException.class)
	public void departureBeforeArrival() {
		new Booking(this.room, ARRIVAL, ARRIVAL.minusDays(1), NIF_BUYER, IBAN_BUYER);
	}

	@Test
	public void arrivalEqualDeparture() {
		new Booking(this.room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER);
	}

}
