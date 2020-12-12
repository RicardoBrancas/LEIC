package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class BuyerConstructorTest extends RollbackTestAbstractClass {
	private static final String ADDRESS = "Somewhere";
	private static final String NAME = "José Vendido";
	private static final String NIF = "123456789";

	IRS irs;

	@Override
	public void populate4Test() {
		this.irs = IRS.getIRSInstance();
	}

	@Test
	public void success() {
		Buyer buyer = new Buyer(this.irs, NIF, NAME, ADDRESS);

		assertEquals(NIF, buyer.getNif());
		assertEquals(NAME, buyer.getName());
		assertEquals(ADDRESS, buyer.getAddress());

		assertEquals(buyer, IRS.getIRSInstance().getTaxPayerByNIF(NIF));
	}

	@Test
	public void uniqueNIF() {
		Buyer seller = new Buyer(this.irs, NIF, NAME, ADDRESS);

		try {
			new Buyer(this.irs, NIF, NAME, ADDRESS);
			fail();
		} catch (TaxException ie) {
			assertEquals(seller, IRS.getIRSInstance().getTaxPayerByNIF(NIF));
		}
	}

	@Test(expected = TaxException.class)
	public void nullNIF() {
		new Buyer(this.irs, null, NAME, ADDRESS);
	}

	@Test(expected = TaxException.class)
	public void emptyNIF() {
		new Buyer(this.irs, "", NAME, ADDRESS);
	}

	@Test(expected = TaxException.class)
	public void nonNineDigitsNIF() {
		new Buyer(this.irs, "12345678", NAME, ADDRESS);
	}

	@Test(expected = TaxException.class)
	public void nullName() {
		new Buyer(this.irs, NIF, null, ADDRESS);
	}

	@Test(expected = TaxException.class)
	public void emptyName() {
		new Buyer(this.irs, NIF, "", ADDRESS);
	}

	@Test(expected = TaxException.class)
	public void nullAddress() {
		new Buyer(this.irs, NIF, NAME, null);
	}

	@Test(expected = TaxException.class)
	public void emptyAddress() {
		new Buyer(this.irs, NIF, NAME, "");
	}

}
