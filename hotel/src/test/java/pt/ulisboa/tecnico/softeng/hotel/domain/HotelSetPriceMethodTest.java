package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelSetPriceMethodTest extends RollbackTestAbstractClass {
	private Hotel hotel;
	private final double price = 25.0;

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", this.price + 5.0, this.price + 10.0);
	}

	@Test
	public void successSingle() {
		this.hotel.setPrice(Room.Type.SINGLE, this.price);
		assertEquals(this.price, this.hotel.getPrice(Room.Type.SINGLE), 0.0d);
	}

	@Test
	public void successDouble() {
		this.hotel.setPrice(Room.Type.DOUBLE, this.price);
		assertEquals(this.price, this.hotel.getPrice(Room.Type.DOUBLE), 0.0d);
	}

	@Test(expected = HotelException.class)
	public void negativePriceSingle() {
		this.hotel.setPrice(Room.Type.SINGLE, -1.0);
	}

	@Test(expected = HotelException.class)
	public void negativePriceDouble() {
		this.hotel.setPrice(Room.Type.DOUBLE, -1.0);
	}

}