package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.car.exception.CarException;

public class RentACarConstructorTest extends RollbackTestAbstractClass {
	private static final String NAME = "eartz";
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";

	@Override
    public void populate4Test() {

    }

	@Test
	public void success() {
		RentACar rentACar = new RentACar(NAME, NIF, IBAN);
		assertEquals(NAME, rentACar.getName());
	}

	@Test(expected = CarException.class)
	public void nullName() {
		new RentACar(null, NIF, IBAN);
	}

	@Test(expected = CarException.class)
	public void emptyName() {
		new RentACar("", NIF, IBAN);
	}
}
