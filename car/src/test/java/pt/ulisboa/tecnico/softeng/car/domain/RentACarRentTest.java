package pt.ulisboa.tecnico.softeng.car.domain;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(JMockit.class)
public class RentACarRentTest extends RollbackTestAbstractClass {

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

	@Mocked
	private BankInterface bankInterface;
	@Mocked
	private TaxInterface taxInterface;

    @Override
    public void populate4Test() {
		rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
		car = new Car(PLATE_CAR, 10, 10, rentACar);
	}

	@Test
	public void rentACarHasCarAvailable() {
		String reference = RentACar.rent(Car.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END);
		assertNotNull(reference);
		assertFalse(car.isFree(BEGIN, END));
	}

	@Test(expected = CarException.class)
	public void rentACarHasNoCarsAvailable() {
		RentACar.rent(Car.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END);
		RentACar.rent(Car.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN.plusDays(1), END.plusDays(1));
	}

	@Test(expected = CarException.class)
	public void noRentACars() {
        rentACar.delete();
        RentACar.rent(Car.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END);
	}

	@Test(expected = CarException.class)
	public void noMotorcycles() {
		RentACar.rent(Motorcycle.class, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END);
	}
}
