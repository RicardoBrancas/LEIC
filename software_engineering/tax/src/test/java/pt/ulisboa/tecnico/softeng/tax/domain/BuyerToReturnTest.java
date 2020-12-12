package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType;
import pt.ulisboa.tecnico.softeng.tax.domain.Seller;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class BuyerToReturnTest extends RollbackTestAbstractClass {
	private static final String SELLER_NIF = "123456789";
	private static final String BUYER_NIF = "987654321";
	private static final String FOOD = "FOOD";
	private static final int TAX = 10;
	private final LocalDate date = new LocalDate(2018, 02, 13);

	private Seller seller;
	private Buyer buyer;
	private ItemType itemType;

	@Override
	public void populate4Test() {
		IRS irs = IRS.getIRSInstance();
		this.seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere");
		this.buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
		this.itemType = new ItemType(irs, FOOD, TAX);
	}

	@Test
	public void success() {
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(50, this.date, this.itemType, this.seller, this.buyer);

		double value = this.buyer.taxReturn(2018);

		assertEquals(1.25f, value, 0.00f);
	}

	@Test
	public void yearWithoutInvoices() {
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(50, this.date, this.itemType, this.seller, this.buyer);

		double value = this.buyer.taxReturn(2017);

		assertEquals(0.0f, value, 0.00f);
	}

	@Test
	public void noInvoices() {
		double value = this.buyer.taxReturn(2018);

		assertEquals(0.0f, value, 0.00f);
	}

	@Test(expected = TaxException.class)
	public void before1970() {
		new Invoice(100, new LocalDate(1969, 02, 13), this.itemType, this.seller, this.buyer);

		assertEquals(0.0f, this.buyer.taxReturn(1969), 0.00f);
	}

	@Test
	public void equal1970() {
		new Invoice(100, new LocalDate(1970, 02, 13), this.itemType, this.seller, this.buyer);
		double value = this.buyer.taxReturn(1970);

		assertEquals(0.5f, value, 0.00f);
	}

	@Test
	public void ignoreCancelled() {
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		Invoice invoice = new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(50, this.date, this.itemType, this.seller, this.buyer);

		invoice.cancel();

		double value = this.buyer.taxReturn(2018);

		assertEquals(0.75f, value, 0.00f);
	}
}
