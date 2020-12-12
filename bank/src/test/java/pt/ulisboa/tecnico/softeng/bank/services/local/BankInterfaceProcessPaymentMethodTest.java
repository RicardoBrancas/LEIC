package pt.ulisboa.tecnico.softeng.bank.services.local;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;

public class BankInterfaceProcessPaymentMethodTest extends RollbackTestAbstractClass {
	private static final String TRANSACTION_SOURCE = "ADVENTURE";
	private static final String TRANSACTION_REFERENCE = "REFERENCE";

	private Bank bank;
	private Account account;
	private String iban;

	@Override
	public void populate4Test() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
		this.iban = this.account.getIBAN();
		this.account.deposit(500);
	}

	@Test
	public void success() {
		this.account.getIBAN();
		String newReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));

		assertNotNull(newReference);
		assertTrue(newReference.startsWith("BK01"));

		assertNotNull(this.bank.getOperation(newReference));
		assertEquals(Operation.Type.WITHDRAW, this.bank.getOperation(newReference).getType());
	}

	@Test
	public void successTwoBanks() {
		Bank otherBank = new Bank("Money", "BK02");
		Client otherClient = new Client(otherBank, "Manuel");
		Account otherAccount = new Account(otherBank, otherClient);
		String otherIban = otherAccount.getIBAN();
		otherAccount.deposit(1000);

		BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
		assertEquals(900, otherAccount.getBalance(), 0.0d);

		BankInterface.processPayment(
				new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"));
		assertEquals(400, this.account.getBalance(), 0.0d);
	}

	@Test
	public void redoAnAlreadyPayed() {
		this.account.getIBAN();
		String firstReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
		String secondReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));

		assertEquals(firstReference, secondReference);
		assertEquals(400, this.account.getBalance(), 0.0d);
	}

	@Test(expected = BankException.class)
	public void nullIban() {
		BankInterface.processPayment(new BankOperationData(null, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
	}

	@Test(expected = BankException.class)
	public void emptyIban() {
		BankInterface.processPayment(new BankOperationData("  ", 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
	}

	@Test(expected = BankException.class)
	public void zeroAmount() {
		BankInterface.processPayment(new BankOperationData(this.iban, 0, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
	}

	@Test
	public void oneAmount() {
		BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));

		assertEquals(499, this.account.getBalance(), 0.0d);
	}

	@Test(expected = BankException.class)
	public void notExistIban() {
		BankInterface.processPayment(new BankOperationData("other", 0, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
	}

	@Test(expected = BankException.class)
	public void nullReference() {
		Bank.processPayment(null, 10);
	}

	@Test(expected = BankException.class)
	public void emptyReference() {
		Bank.processPayment("", 10);
	}

	@Test(expected = BankException.class)
	public void notExistsReference() {
		Bank.processPayment("XPTO", 10);
	}

	@Test(expected = BankException.class)
	public void noBanks() {
		FenixFramework.getDomainRoot().getBankSet().clear();
		Bank.processPayment(this.account.getIBAN(), 10);
	}
}
