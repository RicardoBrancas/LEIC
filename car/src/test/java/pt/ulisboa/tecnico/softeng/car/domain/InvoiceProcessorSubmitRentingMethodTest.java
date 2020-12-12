package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.TaxException;

@RunWith(JMockit.class)
public class InvoiceProcessorSubmitRentingMethodTest extends RollbackTestAbstractClass {
	private static final String CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference";
	private static final String INVOICE_REFERENCE = "InvoiceReference";
	private static final String PAYMENT_REFERENCE = "PaymentReference";

	private static final String PLATE_CAR = "22-33-HZ";
	private static final String DRIVING_LICENSE = "br112233";
	private static final LocalDate date0 = LocalDate.parse("2018-01-05");
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final LocalDate date3 = LocalDate.parse("2018-01-08");
	private static final String RENT_A_CAR_NAME = "Eartz";
	private static final String NIF = "NIF";
	private static final String NIF_CUSTOMER = "NIF1";

	private static final String IBAN = "IBAN";
	private static final String IBAN_CUSTOMER = "IBAN";

	private Car car;

	private RentACar rentACar;

	@Override
	public void populate4Test() {
		this.rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
		this.car = new Car(PLATE_CAR, 10, 10, this.rentACar);
	}

	@Test
	public void success(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);
				this.result = PAYMENT_REFERENCE;
				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		renting.cancel();

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

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		renting.cancel();
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		renting.cancel();
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		renting.cancel();
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

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

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER);
		renting.cancel();
		this.car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER);

		new FullVerifications(taxInterface) {
			{
				TaxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

}
