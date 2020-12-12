package pt.ulisboa.tecnico.softeng.bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class BankPersistenceTest {
	private static final String BANK_NAME = "Money";
	private static final String BANK_CODE = "BK01";
	private static final String CLIENT_NAME = "João dos Anzóis";

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		Bank bank = new Bank(BANK_NAME, BANK_CODE);

		Client client = new Client(bank, CLIENT_NAME);

		Account account = new Account(bank, client);

		account.deposit(100);
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		assertEquals(1, FenixFramework.getDomainRoot().getBankSet().size());

		List<Bank> banks = new ArrayList<>(FenixFramework.getDomainRoot().getBankSet());
		Bank bank = banks.get(0);

		assertEquals(BANK_NAME, bank.getName());
		assertEquals(BANK_CODE, bank.getCode());
		assertEquals(1, bank.getClientSet().size());
		assertEquals(1, bank.getAccountSet().size());
		assertEquals(1, bank.getOperationSet().size());

		List<Client> clients = new ArrayList<>(bank.getClientSet());
		Client client = clients.get(0);

		assertEquals(CLIENT_NAME, client.getName());

		List<Account> accounts = new ArrayList<>(bank.getAccountSet());
		Account account = accounts.get(0);

		assertEquals(client, account.getClient());
		assertNotNull(account.getIBAN());
		assertEquals(100, account.getBalance(), 0.0f);

		List<Operation> operations = new ArrayList<>(bank.getOperationSet());
		Operation operation = operations.get(0);

		assertEquals(Operation.Type.DEPOSIT, operation.getType());
		assertEquals(100, operation.getValue(), 0.0d);
		assertNotNull(operation.getReference());
		assertNotNull(operation.getTime());
	}

	@After
	@Atomic(mode = TxMode.WRITE)
	public void tearDown() {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			bank.delete();
		}
	}

}
