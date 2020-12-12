package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;
import mockit.Mocked;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class RentACarCancelRentingMethodTest extends RollbackTestAbstractClass {
	private static final String PLATE_CAR = "22-33-HZ";
	private static final String RENT_A_CAR_NAME = "Eartz";
	private static final String DRIVING_LICENSE = "lx1423";
	private static final LocalDate BEGIN = LocalDate.parse("2018-01-06");
	private static final LocalDate END = LocalDate.parse("2018-01-09");
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";
    private static final String IBAN_BUYER = "IBAN";
    private RentACar rentACar;
	private Car car;
	private Renting renting;
	
    @Mocked
    private TaxInterface taxInterface;

	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
		car = new Car(PLATE_CAR, 10, 10, rentACar);

		String reference = RentACar.rent(Car.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END);

		renting = RentACar.getRenting(reference);
	}

	@Test
	public void success() {
		String cancel = RentACar.cancelRenting(this.renting.getReference());

		Assert.assertTrue(this.renting.isCancelled());
		Assert.assertEquals(cancel, this.renting.getCancellationReference());
	}

	@Test(expected = CarException.class)
	public void doesNotExist() {
		RentACar.cancelRenting("MISSING_REFERENCE");
	}

	@Test(expected = CarException.class)
	public void nullReference() {
		RentACar.cancelRenting(null);
	}

	@Test(expected = CarException.class)
	public void emptyReference() {
		RentACar.cancelRenting("");
	}
	
	@Test
	public void successIntegration() {
		new Expectations(){
			{
				TaxInterface.cancelInvoice(this.anyString);
			}
		};
		String cancel = RentACar.cancelRenting(this.renting.getReference());

		Assert.assertTrue(this.renting.isCancelled());
		Assert.assertEquals(cancel, this.renting.getCancellationReference());
	}
	
	@Test(expected = CarException.class)
	public void doesNotExistIntegration() {
		new Expectations() {
			{
				TaxInterface.cancelInvoice(this.anyString);
				times = 0;
			}
		};
		RentACar.cancelRenting("MISSING_REFERENCE");
	}
}
