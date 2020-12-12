package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;

@RunWith(JMockit.class)
public class HotelHasVacancyMethodTest extends RollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	private Hotel hotel;
	private Room room;
	private static final String NIF_HOTEL = "123456700";
	private static final String NIF_BUYER = "123456789";
	private static final String IBAN_BUYER = "IBAN_BUYER";

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0);
		this.room = new Room(this.hotel, "01", Type.DOUBLE);
	}

	@Test
	public void hasVacancy() {
		Room room = this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure);

		Assert.assertNotNull(room);
		Assert.assertEquals("01", room.getNumber());
	}

	@Test
	public void noVacancy() {
		this.room.reserve(Type.DOUBLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER);

		assertNull(this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure));
	}

	@Test
	public void noVacancyEmptyRoomSet() {
		Hotel otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0);

		assertNull(otherHotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure));
	}

	@Test(expected = HotelException.class)
	public void nullType() {
		this.hotel.hasVacancy(null, this.arrival, this.departure);
	}

	@Test(expected = HotelException.class)
	public void nullArrival() {
		this.hotel.hasVacancy(Type.DOUBLE, null, this.departure);
	}

	@Test(expected = HotelException.class)
	public void nullDeparture() {
		this.hotel.hasVacancy(Type.DOUBLE, this.arrival, null);
	}

}
