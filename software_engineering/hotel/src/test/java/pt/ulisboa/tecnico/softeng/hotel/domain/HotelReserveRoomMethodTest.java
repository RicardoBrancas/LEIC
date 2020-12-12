package pt.ulisboa.tecnico.softeng.hotel.domain;

import static junit.framework.TestCase.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class HotelReserveRoomMethodTest extends RollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 24);
	private Room room;
	private Hotel hotel;
	private static final String NIF_HOTEL = "123456789";
	private static final String NIF_BUYER = "123456700";
	private static final String IBAN_BUYER = "IBAN_CUSTOMER";
	private static final String IBAN_HOTEL = "IBAN_HOTEL";

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0);
		this.room = new Room(this.hotel, "01", Room.Type.SINGLE);
	}

	@Test
	public void success() {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		final String ref = Hotel.reserveRoom(Room.Type.SINGLE, this.arrival, this.departure, NIF_BUYER, IBAN_BUYER);

		assertTrue(ref != null);
		assertTrue(ref.startsWith("XPTO123"));
	}

	@Test(expected = HotelException.class)
	public void noHotels() {
		FenixFramework.getDomainRoot().getHotelSet().stream().forEach(h -> h.delete());
		Hotel.reserveRoom(Room.Type.SINGLE, this.arrival, this.departure, NIF_BUYER, IBAN_BUYER);
	}

	@Test(expected = HotelException.class)
	public void noVacancy() {
		Hotel.reserveRoom(Room.Type.SINGLE, this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER, IBAN_BUYER);
		Hotel.reserveRoom(Room.Type.SINGLE, this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER, IBAN_BUYER);
	}

	@Test(expected = HotelException.class)
	public void noRooms() {
		this.hotel.getRoomSet().stream().forEach(r -> r.delete());
		Hotel.reserveRoom(Room.Type.SINGLE, this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER, IBAN_BUYER);
	}

}