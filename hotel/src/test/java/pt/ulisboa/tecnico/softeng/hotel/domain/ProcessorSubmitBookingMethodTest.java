package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException;

@RunWith(JMockit.class)
public class ProcessorSubmitBookingMethodTest extends RollbackTestAbstractClass {
	private static LocalDate arrival = new LocalDate(2016, 12, 19);
	private static LocalDate departure = new LocalDate(2016, 12, 24);
	private static LocalDate arrivalTwo = new LocalDate(2016, 12, 25);
	private static LocalDate departureTwo = new LocalDate(2016, 12, 28);
	private final String NIF_HOTEL = "123456700";
	private final String NIF_BUYER = "123456789";
	private final String IBAN_BUYER = "IBAN_BUYER";

	private Hotel hotel;
	private Room room;
	private Booking booking;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", this.NIF_HOTEL, "IBAN", 20.0, 30.0);
		this.room = new Room(this.hotel, "01", Room.Type.SINGLE);
		this.booking = new Booking(this.room, arrival, departure, this.NIF_BUYER, this.IBAN_BUYER);
	}

	@Test
	public void success(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);

		new FullVerifications() {
			{
			}
		};
	}

	@Test
	public void oneTaxFailureOnSubmitInvoice(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new TaxException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrivalTwo, departureTwo, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 4;
			}
		};
	}

	@Test
	public void oneRemoteFailureOnSubmitInvoice(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new RemoteAccessException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrivalTwo, departureTwo, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 4;
			}
		};
	}

	@Test
	public void oneBankFailureOnProcessPayment(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new BankException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrivalTwo, departureTwo, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(bankInterface) {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneRemoteFailureOnProcessPayment(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new RemoteAccessException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrivalTwo, departureTwo, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(bankInterface) {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void successCancel(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.cancelInvoice(this.anyString);
				BankInterface.cancelPayment(this.anyString);
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.booking.cancel();

		new FullVerifications() {
			{
			}
		};
	}

	@Test
	public void oneBankExceptionOnCancelPayment(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.cancelInvoice(this.anyString);
				BankInterface.cancelPayment(this.anyString);
				this.result = new BankException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrival, departure, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(bankInterface) {
			{
				BankInterface.cancelPayment(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneRemoteExceptionOnCancelPayment(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.cancelInvoice(this.anyString);
				BankInterface.cancelPayment(this.anyString);
				this.result = new RemoteAccessException();
				this.result = this.anyString;
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrival, departure, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(bankInterface) {
			{
				BankInterface.cancelPayment(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneTaxExceptionOnCancelInvoice(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);

				BankInterface.cancelPayment(this.anyString);
				TaxInterface.cancelInvoice(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public void delegate() {
						if (this.i < 1) {
							this.i++;
							throw new TaxException();
						}
					}
				};
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrival, departure, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneRemoteExceptionOnCancelInvoice(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.processPayment((RestBankOperationData) this.any);

				BankInterface.cancelPayment(this.anyString);
				TaxInterface.cancelInvoice(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public void delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						}
					}
				};
			}
		};

		this.hotel.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.hotel.getProcessor()
				.submitBooking(new Booking(this.room, arrival, departure, this.NIF_BUYER, this.IBAN_BUYER));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

}