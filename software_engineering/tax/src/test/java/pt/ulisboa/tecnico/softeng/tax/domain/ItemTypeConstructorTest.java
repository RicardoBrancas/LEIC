package pt.ulisboa.tecnico.softeng.tax.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class ItemTypeConstructorTest extends RollbackTestAbstractClass {
	private static final String CAR = "CAR";
	private static final int TAX = 23;

	IRS irs;

	@Override
	public void populate4Test() {
		this.irs = IRS.getIRSInstance();
	}

	@Test
	public void success() {
		IRS irs = IRS.getIRSInstance();

		ItemType itemType = new ItemType(irs, CAR, TAX);

		assertEquals(CAR, itemType.getName());
		assertEquals(TAX, itemType.getTax());
		assertNotNull(IRS.getIRSInstance().getItemTypeByName(CAR));

		assertEquals(itemType, irs.getItemTypeByName(CAR));
	}

	@Test
	public void uniqueName() {
		ItemType itemType = new ItemType(this.irs, CAR, TAX);

		try {
			new ItemType(this.irs, CAR, TAX);
			fail();
		} catch (TaxException te) {
			assertEquals(itemType, IRS.getIRSInstance().getItemTypeByName(CAR));
		}
	}

	@Test(expected = TaxException.class)
	public void nullItemType() {
		new ItemType(this.irs, null, TAX);
	}

	@Test(expected = TaxException.class)
	public void emptyItemType() {
		new ItemType(this.irs, "", TAX);
	}

	@Test(expected = TaxException.class)
	public void negativeTax() {
		new ItemType(this.irs, CAR, -34);
	}

	public void zeroTax() {
		new ItemType(this.irs, CAR, 0);
	}


}
