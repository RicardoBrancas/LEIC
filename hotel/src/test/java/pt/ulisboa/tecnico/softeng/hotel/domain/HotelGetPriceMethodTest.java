package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelGetPriceMethodTest extends RollbackTestAbstractClass {
	private Hotel hotel;
	private final double priceSingle = 20.0;
	private final double priceDouble = 30.0;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", this.priceSingle, this.priceDouble);
	}

	@Test
	public void successSingle() {
		assertEquals(this.priceSingle, this.hotel.getPrice(Room.Type.SINGLE), 0.0d);
	}

	@Test
	public void successDouble() {
		assertEquals(this.priceDouble, this.hotel.getPrice(Room.Type.DOUBLE), 0.0d);
	}

	@Test(expected = HotelException.class)
	public void nullType() {
		this.hotel.getPrice(null);
	}

}