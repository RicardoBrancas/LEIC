package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

public class InvoiceConstructorTest extends RollbackTestAbstractClass {
	private static final String SELLER_NIF = "123456789";
	private static final String BUYER_NIF = "987654321";
	private static final String FOOD = "FOOD";
	private static final int VALUE = 16;
	private static final int TAX = 23;
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
		Invoice invoice = new Invoice(VALUE, this.date, this.itemType, this.seller, this.buyer);

		assertNotNull(invoice.getReference());
		assertEquals(VALUE, invoice.getValue(), 0.0f);
		assertEquals(this.date, invoice.getDate());
		assertEquals(this.itemType, invoice.getItemType());
		assertEquals(this.seller, invoice.getSeller());
		assertEquals(this.buyer, invoice.getBuyer());
		assertEquals(VALUE * TAX / 100.0, invoice.getIva(), 0.00001f);
		assertFalse(invoice.isCancelled());

		assertEquals(invoice, this.seller.getInvoiceByReference(invoice.getReference()));
		assertEquals(invoice, this.buyer.getInvoiceByReference(invoice.getReference()));
	}

	@Test(expected = TaxException.class)
	public void nullSeller() {
		new Invoice(VALUE, this.date, this.itemType, null, this.buyer);
	}

	@Test(expected = TaxException.class)
	public void nullBuyer() {
		new Invoice(VALUE, this.date, this.itemType, this.seller, null);
	}

	@Test(expected = TaxException.class)
	public void nullItemType() {
		new Invoice(VALUE, this.date, null, this.seller, this.buyer);
	}

	@Test(expected = TaxException.class)
	public void zeroValue() {
		new Invoice(0, this.date, this.itemType, this.seller, this.buyer);
	}

	@Test(expected = TaxException.class)
	public void negativeValue() {
		new Invoice(-23.6f, this.date, this.itemType, this.seller, this.buyer);
	}

	@Test(expected = TaxException.class)
	public void nullDate() {
		new Invoice(VALUE, null, this.itemType, this.seller, this.buyer);
	}

	@Test(expected = TaxException.class)
	public void before1970() {
		new Invoice(VALUE, new LocalDate(1969, 12, 31), this.itemType, this.seller, this.buyer);
	}

	public void equal1970() {
		new Invoice(VALUE, new LocalDate(1970, 01, 01), this.itemType, this.seller, this.buyer);
	}
}
