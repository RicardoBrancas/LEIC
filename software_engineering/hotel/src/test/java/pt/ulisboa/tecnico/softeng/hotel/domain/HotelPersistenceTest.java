package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;

public class HotelPersistenceTest {
	private static Logger logger = LoggerFactory.getLogger(HotelPersistenceTest.class);

	private static final String HOTEL_NIF = "123456789";
	private static final String HOTEL_IBAN = "IBAN";
	private static final String HOTEL_NAME = "Berlin Plaza";
	private final static String HOTEL_CODE = "H123456";
	private static final String ROOM_NUMBER = "01";
	private static final String CLIENT_NIF = "123458789";
	private static final String CLIENT_IBAN = "IBANC";

	private final LocalDate arrival = new LocalDate(2017, 12, 15);
	private final LocalDate departure = new LocalDate(2017, 12, 19);

	@Before
	@Atomic(mode = TxMode.WRITE)
	public void setUp() {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete();
		}
	}

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		Hotel hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10.0, 20.0);

		new Room(hotel, ROOM_NUMBER, Type.DOUBLE);

		Hotel.reserveRoom(Type.DOUBLE, this.arrival, this.departure, CLIENT_NIF, CLIENT_IBAN);
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		assertEquals(1, FenixFramework.getDomainRoot().getHotelSet().size());

		List<Hotel> hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet());
		Hotel hotel = hotels.get(0);

		assertEquals(HOTEL_NAME, hotel.getName());
		assertEquals(HOTEL_CODE, hotel.getCode());
		assertEquals(HOTEL_IBAN, hotel.getIban());
		assertEquals(HOTEL_NIF, hotel.getNif());
		assertEquals(10.0, hotel.getPriceSingle(), 0.0d);
		assertEquals(20.0, hotel.getPriceDouble(), 0.0d);
		assertEquals(1, hotel.getRoomSet().size());
		Processor processor = hotel.getProcessor();
		assertNotNull(processor);
		assertEquals(1, processor.getBookingSet().size());

		List<Room> rooms = new ArrayList<>(hotel.getRoomSet());
		Room room = rooms.get(0);

		assertEquals(ROOM_NUMBER, room.getNumber());
		assertEquals(Type.DOUBLE, room.getType());
		assertEquals(1, room.getBookingSet().size());

		List<Booking> bookings = new ArrayList<>(room.getBookingSet());
		Booking booking = bookings.get(0);

		assertNotNull(booking.getReference());
		assertEquals(this.arrival, booking.getArrival());
		assertEquals(this.departure, booking.getDeparture());
		assertEquals(CLIENT_IBAN, booking.getBuyerIban());
		assertEquals(CLIENT_NIF, booking.getBuyerNif());
		assertEquals(HOTEL_NIF, booking.getProviderNif());
		assertEquals(80.0, booking.getPrice(), 0.0d);
		assertEquals(room, booking.getRoom());
		assertNotNull(booking.getTime());
		assertNotNull(booking.getProcessor());
	}

	@After
	@Atomic(mode = TxMode.WRITE)
	public void tearDown() {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete();
		}
	}

}
