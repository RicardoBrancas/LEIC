package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelConstructorTest extends RollbackTestAbstractClass {
	private static final String IBAN = "IBAN";
	private static final String NIF = "NIF";

	private static final String HOTEL_NAME = "Londres";
	private static final String HOTEL_CODE = "XPTO123";

	private static final double PRICE_SINGLE = 20.0;
	private static final double PRICE_DOUBLE = 30.0;

	@Override
	public void populate4Test() {
	}

	@Test
	public void success() {

		Hotel hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);

		Assert.assertEquals(HOTEL_NAME, hotel.getName());
		Assert.assertTrue(hotel.getCode().length() == Hotel.CODE_SIZE);
		Assert.assertEquals(0, hotel.getRoomSet().size());
		Assert.assertEquals(1, FenixFramework.getDomainRoot().getHotelSet().size());
		Assert.assertEquals(PRICE_SINGLE, hotel.getPrice(Room.Type.SINGLE), 0.0d);
		Assert.assertEquals(PRICE_DOUBLE, hotel.getPrice(Room.Type.DOUBLE), 0.0d);
	}

	@Test(expected = HotelException.class)
	public void nullCode() {
		new Hotel(null, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void blankCode() {
		new Hotel("      ", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void emptyCode() {
		new Hotel("", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void nullName() {
		new Hotel(HOTEL_CODE, null, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void blankName() {
		new Hotel(HOTEL_CODE, "  ", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void emptyName() {
		new Hotel(HOTEL_CODE, "", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void codeSizeLess() {
		new Hotel("123456", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void codeSizeMore() {
		new Hotel("12345678", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void codeNotUnique() {
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
		new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void nifNotUnique() {
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
		new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void negativePriceSingle() {
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, -1.0, PRICE_DOUBLE);
	}

	@Test(expected = HotelException.class)
	public void negativePriceDouble() {
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, -1.0);
	}

}
