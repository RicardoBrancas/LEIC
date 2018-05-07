package pt.ulisboa.tecnico.softeng.hotel.services.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class HotelInterfaceBulkBookingMethodTest extends RollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	private Hotel hotel;
	private final String NIF_BUYER = "123456789";
	private final String IBAN_BUYER = "IBAN_BUYER";
	private final String BULK_ID = "BULK_ID";

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0);

		new Room(this.hotel, "01", Type.DOUBLE);
		new Room(this.hotel, "02", Type.SINGLE);
		new Room(this.hotel, "03", Type.DOUBLE);
		new Room(this.hotel, "04", Type.SINGLE);

		this.hotel = new Hotel("XPTO124", "Paris", "NIF2", "IBAN2", 25.0, 35.0);
		new Room(this.hotel, "01", Type.DOUBLE);
		new Room(this.hotel, "02", Type.SINGLE);
		new Room(this.hotel, "03", Type.DOUBLE);
		new Room(this.hotel, "04", Type.SINGLE);
	}

	@Test
	public void success() {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		Set<String> references = HotelInterface.bulkBooking(2, this.arrival, this.departure, this.NIF_BUYER,
				this.IBAN_BUYER, this.BULK_ID);

		assertEquals(2, references.size());
	}

	@Test(expected = HotelException.class)
	public void zeroNumber() {
		HotelInterface.bulkBooking(0, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID);
	}

	@Test(expected = HotelException.class)
	public void noRooms() {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete();
		}

		this.hotel = new Hotel("XPTO124", "Paris", "NIF", "IBAN", 27.0, 37.0);

		HotelInterface.bulkBooking(3, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID);
	}

	@Test
	public void OneNumber() {
		Set<String> references = HotelInterface.bulkBooking(1, this.arrival, this.departure, this.NIF_BUYER,
				this.IBAN_BUYER, this.BULK_ID);

		assertEquals(1, references.size());
	}

	@Test(expected = HotelException.class)
	public void nullArrival() {
		HotelInterface.bulkBooking(2, null, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID);
	}

	@Test(expected = HotelException.class)
	public void nullDeparture() {
		HotelInterface.bulkBooking(2, this.arrival, null, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID);
	}

	@Test
	public void reserveAll() {
		Set<String> references = HotelInterface.bulkBooking(8, this.arrival, this.departure, this.NIF_BUYER,
				this.IBAN_BUYER, this.BULK_ID);

		assertEquals(8, references.size());
	}

	@Test
	public void reserveAllPlusOne() {
		try {
			HotelInterface.bulkBooking(9, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID);
			fail();
		} catch (HotelException he) {
			assertEquals(8, HotelInterface.getAvailableRooms(8, this.arrival, this.departure).size());
		}
	}

	@Test
	public void idempotentBulkBooking() {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		Set<String> references = HotelInterface.bulkBooking(4, this.arrival, this.departure, this.NIF_BUYER,
				this.IBAN_BUYER, this.BULK_ID);

		assertEquals(4, references.size());

		Set<String> equalReferences = HotelInterface.bulkBooking(4, this.arrival, this.departure, this.NIF_BUYER,
				this.IBAN_BUYER, this.BULK_ID);

		assertEquals(4, HotelInterface.getAvailableRooms(4, this.arrival, this.departure).size());
		assertEquals(references.stream().sorted().collect(Collectors.toList()),
				equalReferences.stream().sorted().collect(Collectors.toList()));
	}

}
