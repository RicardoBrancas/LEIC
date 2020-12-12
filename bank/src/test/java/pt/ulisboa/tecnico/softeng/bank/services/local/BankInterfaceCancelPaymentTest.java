package pt.ulisboa.tecnico.softeng.bank.services.local;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankInterfaceCancelPaymentTest extends RollbackTestAbstractClass {
	private Bank bank;
	private Account account;
	private String reference;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
		this.reference = this.account.deposit(100).getReference();
	}

	@Test
	public void success() {
		String newReference = BankInterface.cancelPayment(this.reference);

		assertNotNull(this.bank.getOperation(newReference));
	}

	@Test(expected = BankException.class)
	public void nullReference() {
		BankInterface.cancelPayment(null);
	}

	@Test(expected = BankException.class)
	public void emptyReference() {
		BankInterface.cancelPayment("");
	}

	@Test(expected = BankException.class)
	public void notExistsReference() {
		BankInterface.cancelPayment("XPTO");
	}

}
