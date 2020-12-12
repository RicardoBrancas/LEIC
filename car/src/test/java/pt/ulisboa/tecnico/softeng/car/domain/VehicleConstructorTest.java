package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;

public class VehicleConstructorTest extends RollbackTestAbstractClass {
	private static final String PLATE_CAR = "22-33-HZ";
	private static final String PLATE_MOTORCYCLE = "44-33-HZ";
	private static final String RENT_A_CAR_NAME = "Eartz";
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";
	private RentACar rentACar;

    @Override
	public void populate4Test() {
		this.rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
	}

	@Test
    public void success() {
		Vehicle car = new Car(PLATE_CAR, 10, 10, this.rentACar);
		Vehicle motorcycle = new Motorcycle(PLATE_MOTORCYCLE, 10, 10, this.rentACar);

		assertEquals(PLATE_CAR, car.getPlate());
		assertTrue(this.rentACar.hasVehicle(PLATE_CAR));
		assertEquals(PLATE_MOTORCYCLE, motorcycle.getPlate());
		assertTrue(this.rentACar.hasVehicle(PLATE_MOTORCYCLE));
		assertTrue(10.0 == car.getPrice());
	}

	@Test(expected = CarException.class)
	public void emptyLicensePlate() {
		new Car("", 10, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void nullLicensePlate() {
		new Car(null, 10, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void invalidLicensePlate() {
		new Car("AA-XX-a", 10, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void invalidLicensePlate2() {
		new Car("AA-XX-aaa", 10, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void duplicatedPlate() {
		new Car(PLATE_CAR, 0, 10, this.rentACar);
		new Car(PLATE_CAR, 0, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void duplicatedPlateDifferentRentACar() {
		new Car(PLATE_CAR, 0, 10, rentACar);
		RentACar rentACar2 = new RentACar(RENT_A_CAR_NAME + "2", NIF, IBAN);
		new Car(PLATE_CAR, 2, 10, rentACar2);
	}

	@Test(expected = CarException.class)
	public void negativeKilometers() {
		new Car(PLATE_CAR, -1, 10, this.rentACar);
	}

	@Test(expected = CarException.class)
	public void noRentACar() {
		new Car(PLATE_CAR, 0, 10, null);
	}

}
