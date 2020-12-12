package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException;

@RunWith(JMockit.class)
public class InvoiceProcessorSubmitBookingMethodTest extends RollbackTestAbstractClass {
	private static final String CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference";
	private static final String INVOICE_REFERENCE = "InvoiceReference";
	private static final String PAYMENT_REFERENCE = "PaymentReference";
	private static final int AMOUNT = 30;
	private static final String IBAN = "IBAN";
	private static final String NIF = "123456789";
	private ActivityProvider provider;
	private ActivityOffer offer;
	private Booking booking;

	@Override
	public void populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 10);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end, AMOUNT);
		this.booking = new Booking(this.provider, this.offer, NIF, IBAN);
	}

	@Test
	public void success(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);

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
				this.result = PAYMENT_REFERENCE;
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new TaxException();
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneRemoteFailureOnSubmitInvoice(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = PAYMENT_REFERENCE;
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new RemoteAccessException();
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneBankFailureOnProcessPayment(@Mocked final TaxInterface taxInterface,
			@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new BankException();
				this.result = PAYMENT_REFERENCE;
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

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
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new RemoteAccessException();
				this.result = PAYMENT_REFERENCE;
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

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

		this.provider.getProcessor().submitBooking(this.booking);
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

				BankInterface.cancelPayment(this.anyString);
				this.result = new BankException();
				this.result = CANCEL_PAYMENT_REFERENCE;
				TaxInterface.cancelInvoice(this.anyString);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

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

				BankInterface.cancelPayment(this.anyString);
				this.result = new RemoteAccessException();
				this.result = CANCEL_PAYMENT_REFERENCE;
				TaxInterface.cancelInvoice(this.anyString);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

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
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
				BankInterface.cancelPayment(this.anyString);
				this.result = CANCEL_PAYMENT_REFERENCE;
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

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

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
				BankInterface.processPayment((RestBankOperationData) this.any);
				TaxInterface.submitInvoice((RestInvoiceData) this.any);

				BankInterface.cancelPayment(this.anyString);
				this.result = CANCEL_PAYMENT_REFERENCE;
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

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				TaxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

}
