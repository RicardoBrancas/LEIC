package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class AccountDepositMethodTest extends RollbackTestAbstractClass {
	private Bank bank;
	private Account account;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
	}

	@Test
	public void success() {
		String reference = this.account.deposit(50).getReference();

		Assert.assertEquals(50, this.account.getBalance(), 0);
		Operation operation = this.bank.getOperation(reference);
		Assert.assertNotNull(operation);
		Assert.assertEquals(Operation.Type.DEPOSIT, operation.getType());
		Assert.assertEquals(this.account, operation.getAccount());
		Assert.assertEquals(50, operation.getValue(), 0);
	}

	@Test(expected = BankException.class)
	public void zeroAmount() {
		this.account.deposit(0);
	}

	@Test
	public void oneAmount() {
		this.account.deposit(1);
	}

	@Test(expected = BankException.class)
	public void negativeAmount() {
		this.account.deposit(-100);
	}

}
