package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.Seller;
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;

public class IRSGetTaxPayerByNIFTest extends RollbackTestAbstractClass {
	private static final String SELLER_NIF = "123456789";
	private static final String BUYER_NIF = "987654321";

	private IRS irs;

	@Override
	public void populate4Test() {
		this.irs = IRS.getIRSInstance();
		new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere");
		new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
	}

	@Test
	public void successBuyer() {
		TaxPayer taxPayer = this.irs.getTaxPayerByNIF(BUYER_NIF);

		assertNotNull(taxPayer);
		assertEquals(BUYER_NIF, taxPayer.getNif());
	}

	@Test
	public void successSeller() {
		TaxPayer taxPayer = this.irs.getTaxPayerByNIF(SELLER_NIF);

		assertNotNull(taxPayer);
		assertEquals(SELLER_NIF, taxPayer.getNif());
	}

	@Test
	public void nullNIF() {
		TaxPayer taxPayer = this.irs.getTaxPayerByNIF(null);

		assertNull(taxPayer);
	}

	@Test
	public void emptyNIF() {
		TaxPayer taxPayer = this.irs.getTaxPayerByNIF("");

		assertNull(taxPayer);
	}

	@Test
	public void doesNotExist() {
		TaxPayer taxPayer = this.irs.getTaxPayerByNIF("122456789");

		assertNull(taxPayer);
	}
}
