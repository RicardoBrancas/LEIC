package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.*;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;

@RunWith(JMockit.class)
public class RentingConflictTest extends RollbackTestAbstractClass {
	private static final String PLATE_CAR = "22-33-HZ";
	private static final String DRIVING_LICENSE = "br112233";
	private static final LocalDate date0 = LocalDate.parse("2018-01-05");
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final LocalDate date3 = LocalDate.parse("2018-01-08");
	private static final LocalDate date4 = LocalDate.parse("2018-01-09");
	private static final String RENT_A_CAR_NAME = "Eartz";
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";
    private static final String IBAN_BUYER = "IBAN";
    private Car car;

	@Mocked
	private BankInterface bankInterface;
	@Mocked
	private TaxInterface taxInterface;

    @Override
    public void populate4Test() {
		RentACar rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
		this.car = new Car(PLATE_CAR, 10, 10, rentACar);
	}

	@Test()
	public void retingIsBeforeDates() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		assertFalse(renting.conflict(date3, date4));
	}

	@Test()
	public void retingIsBeforeDatesSameDayInterval() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		assertFalse(renting.conflict(date3, date3));
	}

	@Test()
	public void rentingEndsOnStartDate() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		assertTrue(renting.conflict(date2, date3));
	}

	@Test()
	public void rentingStartsOnEndDate() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		assertTrue(renting.conflict(date1, date1));
	}

	@Test()
	public void rentingStartsDuringInterval() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		assertTrue(renting.conflict(date0, date3));
	}

	@Test(expected = CarException.class)
	public void endBeforeBegin() {
		Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER);
		renting.conflict(date2, date1);
	}

}
