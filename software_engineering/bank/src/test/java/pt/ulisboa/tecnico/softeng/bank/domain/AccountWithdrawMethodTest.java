package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class AccountWithdrawMethodTest extends RollbackTestAbstractClass {
	private Bank bank;
	private Account account;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
		this.account.deposit(100);
	}

	@Test
	public void success() {
		String reference = this.account.withdraw(40).getReference();

		Assert.assertEquals(60, this.account.getBalance(), 0);
		Operation operation = this.bank.getOperation(reference);
		Assert.assertNotNull(operation);
		Assert.assertEquals(Operation.Type.WITHDRAW, operation.getType());
		Assert.assertEquals(this.account, operation.getAccount());
		Assert.assertEquals(40, operation.getValue(), 0);
	}

	@Test(expected = BankException.class)
	public void negativeAmount() {
		this.account.withdraw(-20);
	}

	@Test(expected = BankException.class)
	public void zeroAmount() {
		this.account.withdraw(0);
	}

	@Test
	public void oneAmount() {
		this.account.withdraw(1);
		Assert.assertEquals(99, this.account.getBalance(), 0);
	}

	@Test
	public void equalToBalance() {
		this.account.withdraw(100);
		Assert.assertEquals(0, this.account.getBalance(), 0);
	}

	@Test(expected = BankException.class)
	public void equalToBalancePlusOne() {
		this.account.withdraw(101);
	}

	@Test(expected = BankException.class)
	public void moreThanBalance() {
		this.account.withdraw(150);
	}

}
