package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankConstructorTest extends RollbackTestAbstractClass {
	private static final String BANK_CODE = "BK01";
	private static final String BANK_NAME = "Money";

	@Override
	public void populate4Test() {
	}

	@Test
	public void success() {
		Bank bank = new Bank(BANK_NAME, BANK_CODE);

		Assert.assertEquals(BANK_NAME, bank.getName());
		Assert.assertEquals(BANK_CODE, bank.getCode());
		Assert.assertEquals(1, FenixFramework.getDomainRoot().getBankSet().size());
		Assert.assertEquals(0, bank.getAccountSet().size());
		Assert.assertEquals(0, bank.getClientSet().size());
	}

	@Test(expected = BankException.class)
	public void nullName() {
		new Bank(null, BANK_CODE);
	}

	@Test(expected = BankException.class)
	public void emptyName() {
		new Bank("    ", BANK_CODE);
	}

	@Test(expected = BankException.class)
	public void nullCode() {
		new Bank(BANK_NAME, null);
	}

	@Test(expected = BankException.class)
	public void emptyCode() {
		new Bank(BANK_NAME, "    ");
	}

	@Test(expected = BankException.class)
	public void inconsistentCodeSmaller() {
		new Bank(BANK_NAME, "BK0");
	}

	@Test(expected = BankException.class)
	public void inconsistentCodeBigger() {
		new Bank(BANK_NAME, "BK011");
	}

	@Test
	public void notUniqueCode() {
		new Bank(BANK_NAME, BANK_CODE);
		try {
			new Bank(BANK_NAME, BANK_CODE);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getBankSet().size());
		}
	}

}
