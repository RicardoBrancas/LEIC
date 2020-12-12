package pt.ulisboa.tecnico.softeng.bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class OperationRevertMethodTest extends RollbackTestAbstractClass {
	private Bank bank;
	private Account account;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
	}

	@Test
	public void revertDeposit() {
		String reference = this.account.deposit(100).getReference();
		Operation operation = this.bank.getOperation(reference);

		String newReference = operation.revert();

		assertEquals(0, this.account.getBalance(), 0);
		assertNotNull(this.bank.getOperation(newReference));
		assertNotNull(this.bank.getOperation(reference));
	}

	@Test
	public void revertWithdraw() {
		this.account.deposit(1000);
		String reference = this.account.withdraw(100).getReference();
		Operation operation = this.bank.getOperation(reference);

		String newReference = operation.revert();

		assertEquals(1000, this.account.getBalance(), 0);
		assertNotNull(this.bank.getOperation(newReference));
		assertNotNull(this.bank.getOperation(reference));
	}

}
