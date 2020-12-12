package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class TaxPersistenceTest {
	private static final String SELLER_NIF = "123456789";
	private static final String BUYER_NIF = "987654321";
	private static final String FOOD = "FOOD";
	private static final int VALUE = 16;
	private final LocalDate date = new LocalDate(2018, 02, 13);

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public void atomicProcess() {
		IRS irs = IRS.getIRSInstance();
		Seller seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere");
		Buyer buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
		ItemType it = new ItemType(irs, FOOD, VALUE);

		new Invoice(VALUE, this.date, it, seller, buyer);
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public void atomicAssert() {
		IRS irs = IRS.getIRSInstance();

		assertEquals(2, irs.getTaxPayerSet().size());

		TaxPayer taxPayer1 = new ArrayList<>(irs.getTaxPayerSet()).get(0);
		if (taxPayer1 instanceof Seller) {
			assertEquals(SELLER_NIF, taxPayer1.getNif());
		} else {
			assertEquals(BUYER_NIF, taxPayer1.getNif());
		}

		TaxPayer taxPayer2 = new ArrayList<>(irs.getTaxPayerSet()).get(1);
		if (taxPayer2 instanceof Seller) {
			assertEquals(SELLER_NIF, taxPayer2.getNif());
		} else {
			assertEquals(BUYER_NIF, taxPayer2.getNif());
		}

		assertEquals(1, irs.getItemTypeSet().size());
		ItemType itemType = new ArrayList<>(irs.getItemTypeSet()).get(0);
		assertEquals(VALUE, itemType.getTax());
		assertEquals(FOOD, itemType.getName());

		assertEquals(1, irs.getInvoiceSet().size());
		Invoice invoice = new ArrayList<>(irs.getInvoiceSet()).get(0);
		assertEquals(VALUE, invoice.getValue(), 0);
		assertNotNull(invoice.getReference());
		assertEquals(this.date, invoice.getDate());
		assertEquals(BUYER_NIF, invoice.getBuyer().getNif());
		assertEquals(SELLER_NIF, invoice.getSeller().getNif());
		assertEquals(itemType, invoice.getItemType());
		assertNotNull(invoice.getTime());
		assertFalse(invoice.getCancelled());
	}

	@After
	@Atomic(mode = Atomic.TxMode.WRITE)
	public void tearDown() {
		FenixFramework.getDomainRoot().getIrs().delete();
	}
}
