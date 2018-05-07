package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType;
import pt.ulisboa.tecnico.softeng.tax.domain.Seller;

public class TaxPayerGetTaxesPerYearMethodsTest extends RollbackTestAbstractClass {
	private static final String SELLER_NIF = "123456788";
	private static final String BUYER_NIF = "987654311";
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
		new Invoice(100, new LocalDate(2017, 12, 12), this.itemType, this.seller, this.buyer);
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(100, this.date, this.itemType, this.seller, this.buyer);
		new Invoice(50, this.date, this.itemType, this.seller, this.buyer);

		Map<Integer, Double> toPay = this.seller.getToPayPerYear();

		assertEquals(2, toPay.keySet().size());
		assertEquals(10.0d, toPay.get(2017), 0.00d);
		assertEquals(25.0d, toPay.get(2018), 0.00d);

		Map<Integer, Double> taxReturn = this.buyer.getTaxReturnPerYear();

		assertEquals(2, taxReturn.keySet().size());
		assertEquals(0.5d, taxReturn.get(2017), 0.00d);
		assertEquals(1.25d, taxReturn.get(2018), 0.00d);
	}

	@Test
	public void successEmpty() {
		Map<Integer, Double> toPay = this.seller.getToPayPerYear();

		assertEquals(0, toPay.keySet().size());

		Map<Integer, Double> taxReturn = this.buyer.getTaxReturnPerYear();

		assertEquals(0, taxReturn.keySet().size());
	}

}
