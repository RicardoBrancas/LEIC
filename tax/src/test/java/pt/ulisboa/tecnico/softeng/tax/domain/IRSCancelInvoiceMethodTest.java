package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertTrue;

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

public class IRSCancelInvoiceMethodTest extends RollbackTestAbstractClass {
	private static final String SELLER_NIF = "123456789";
	private static final String BUYER_NIF = "987654321";
	private static final String FOOD = "FOOD";
	private static final int VALUE = 16;
	private final LocalDate date = new LocalDate(2018, 02, 13);

	private IRS irs;
	private String reference;
	Invoice invoice;

	@Override
	public void populate4Test() {
		this.irs = IRS.getIRSInstance();
		Seller seller = new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere");
		Buyer buyer = new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
		ItemType itemType = new ItemType(this.irs, FOOD, VALUE);
		this.invoice = new Invoice(30.0, this.date, itemType, seller, buyer);
		this.reference = this.invoice.getReference();
	}

	@Test
	public void success() {
		IRS.cancelInvoice(this.reference);

		assertTrue(this.invoice.isCancelled());
	}

	@Test(expected = TaxException.class)
	public void nullReference() {
		IRS.cancelInvoice(null);
	}

	@Test(expected = TaxException.class)
	public void emptyReference() {
		IRS.cancelInvoice("   ");
	}

	@Test(expected = TaxException.class)
	public void referenceDoesNotExist() {
		IRS.cancelInvoice("XXXXXXXX");
	}

}
