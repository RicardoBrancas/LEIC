package pt.ulisboa.tecnico.softeng.bank.domain;

import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankGetAccountMethodTest extends RollbackTestAbstractClass {
	Bank bank;
	Client client;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
	}

	@Test
	public void success() {
		Account account = new Account(this.bank, this.client);

		Account result = this.bank.getAccount(account.getIBAN());

		Assert.assertEquals(account, result);
	}

	@Test(expected = BankException.class)
	public void nullIBAN() {
		this.bank.getAccount(null);
	}

	@Test(expected = BankException.class)
	public void emptyIBAN() {
		this.bank.getAccount("");
	}

	@Test(expected = BankException.class)
	public void blankIBAN() {
		this.bank.getAccount("    ");
	}

	@Test
	public void emptySetOfAccounts() {
		assertNull(this.bank.getAccount("XPTO"));
	}

	@Test
	public void severalAccountsDoNoMatch() {
		new Account(this.bank, this.client);
		new Account(this.bank, this.client);

		assertNull(this.bank.getAccount("XPTO"));

	}

}
