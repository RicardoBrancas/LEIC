package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class ClientContructorMethodTest extends RollbackTestAbstractClass {
	private static final String CLIENT_NAME = "António";

	private Bank bank;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
	}

	@Test
	public void success() {
		Client client = new Client(this.bank, CLIENT_NAME);

		Assert.assertEquals(CLIENT_NAME, client.getName());
		Assert.assertTrue(client.getID().length() >= 1);
		Assert.assertTrue(this.bank.getClientSet().contains(client));
	}

	@Test(expected = BankException.class)
	public void nullBank() {
		new Client(null, CLIENT_NAME);
	}

	@Test(expected = BankException.class)
	public void nullClientName() {
		new Client(this.bank, null);
	}

	@Test(expected = BankException.class)
	public void blankClientName() {
		new Client(this.bank, "    ");
	}

	@Test(expected = BankException.class)
	public void emptyClientName() {
		new Client(this.bank, "");
	}

}
