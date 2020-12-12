package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.*;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class VehicleIsFreeTest extends RollbackTestAbstractClass {

	private static final String PLATE_CAR = "22-33-HZ";
	private static final String RENT_A_CAR_NAME = "Eartz";
	private static final String DRIVING_LICENSE = "lx1423";
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final LocalDate date3 = LocalDate.parse("2018-01-08");
	private static final LocalDate date4 = LocalDate.parse("2018-01-09");
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

	@Test
	public void noBookingWasMade() {
		assertTrue(car.isFree(date1, date2));
		assertTrue(car.isFree(date1, date3));
		assertTrue(car.isFree(date3, date4));
		assertTrue(car.isFree(date4, date4));
	}

	@Test
	public void bookingsWereMade() {
		car.rent(DRIVING_LICENSE, date2, date2, NIF, IBAN_BUYER);
		car.rent(DRIVING_LICENSE, date3, date4, NIF, IBAN_BUYER);

		assertFalse(car.isFree(date1, date2));
		assertFalse(car.isFree(date1, date3));
		assertFalse(car.isFree(date3, date4));
		assertFalse(car.isFree(date4, date4));
		assertTrue(car.isFree(date1, date1));
	}

}
