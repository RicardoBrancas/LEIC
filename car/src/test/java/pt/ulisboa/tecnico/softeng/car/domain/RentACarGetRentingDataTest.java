package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.*;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;

@RunWith(JMockit.class)
public class RentACarGetRentingDataTest extends RollbackTestAbstractClass {

	private static final String NAME1 = "eartz";
	private static final String PLATE_CAR1 = "aa-00-11";
	private static final String DRIVING_LICENSE = "br123";
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
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
		RentACar rentACar1 = new RentACar(NAME1, NIF, IBAN);
		this.car = new Car(PLATE_CAR1, 10, 10, rentACar1);
	}

	@Test
	public void success() {
		Renting renting = car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER);
		RentingData rentingData = RentACar.getRentingData(renting.getReference());
		assertEquals(renting.getReference(), rentingData.getReference());
		assertEquals(DRIVING_LICENSE, rentingData.getDrivingLicense());
		assertEquals(0, PLATE_CAR1.compareToIgnoreCase(rentingData.getPlate()));
		assertEquals(this.car.getRentACar().getCode(), rentingData.getRentACarCode());
	}

	@Test(expected = CarException.class)
	public void getRentingDataFail() {
		RentACar.getRentingData("1");
	}
}
