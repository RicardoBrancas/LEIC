package pt.ulisboa.tecnico.softeng.bank.services.local;

import java.util.List;
import java.util.stream.Collectors;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.AccountData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.ClientData;

public class BankInterface {

	@Atomic(mode = TxMode.READ)

	public static List<BankData> getBanks() {
		return FenixFramework.getDomainRoot().getBankSet().stream()
				.sorted((b1, b2) -> b1.getName().compareTo(b2.getName())).map(b -> new BankData(b))
				.collect(Collectors.toList());
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createBank(BankData bankData) {
		new Bank(bankData.getName(), bankData.getCode());
	}

	@Atomic(mode = TxMode.READ)
	public static BankData getBankDataByCode(String code) {
		Bank bank = getBankByCode(code);
		if (bank == null) {
			return null;
		}

		return new BankData(bank);
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createClient(String code, ClientData client) {
		Bank bank = getBankByCode(code);
		if (bank == null) {
			throw new BankException();
		}

		new Client(bank, client.getName());
	}

	@Atomic(mode = TxMode.READ)
	public static ClientData getClientDataById(String code, String id) {
		Bank bank = getBankByCode(code);
		if (bank == null) {
			return null;
		}

		Client client = bank.getClientById(id);
		if (client == null) {
			return null;
		}

		return new ClientData(client);
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createAccount(String code, String id) {
		Bank bank = getBankByCode(code);
		if (bank == null) {
			throw new BankException();
		}

		Client client = bank.getClientById(id);
		if (client == null) {
			throw new BankException();
		}

		new Account(bank, client);
	}

	@Atomic(mode = TxMode.READ)
	public static AccountData getAccountData(String iban) {
		Account account = getAccountByIban(iban);
		if (account == null) {
			throw new BankException();
		}

		return new AccountData(account);
	}

	@Atomic(mode = TxMode.WRITE)
	public static void deposit(String iban, double amount) {
		Account account = getAccountByIban(iban);
		if (account == null) {
			throw new BankException();
		}

		account.deposit(amount);
	}

	@Atomic(mode = TxMode.WRITE)
	public static void withdraw(String iban, double amount) {
		Account account = getAccountByIban(iban);
		if (account == null) {
			throw new BankException();
		}

		account.withdraw(amount);
	}

	@Atomic(mode = TxMode.WRITE)
	public static String processPayment(BankOperationData bankOperationData) {
		Operation operation = getOperationBySourceAndReference(bankOperationData.getTransactionSource(),
				bankOperationData.getTransactionReference());
		if (operation != null) {
			return operation.getReference();
		}

		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			Account account = bank.getAccount(bankOperationData.getIban());
			if (account != null) {
				Operation newOperation = account.withdraw(bankOperationData.getValue());
				newOperation.setTransactionSource(bankOperationData.getTransactionSource());
				newOperation.setTransactionReference(bankOperationData.getTransactionReference());
				return newOperation.getReference();
			}
		}
		throw new BankException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static String cancelPayment(String paymentConfirmation) {
		Operation operation = getOperationByReference(paymentConfirmation);
		if (operation != null && operation.getCancellation() == null) {
			return operation.revert();
		}
		throw new BankException();
	}

	@Atomic(mode = TxMode.READ)
	public static BankOperationData getOperationData(String reference) {
		Operation operation = getOperationByReference(reference);
		if (operation != null) {
			return new BankOperationData(operation);
		}
		throw new BankException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static void deleteBanks() {
		FenixFramework.getDomainRoot().getBankSet().stream().forEach(b -> b.delete());
	}

	private static Operation getOperationByReference(String reference) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			Operation operation = bank.getOperation(reference);
			if (operation != null) {
				return operation;
			}
		}
		return null;
	}

	private static Operation getOperationBySourceAndReference(String transactionSource, String transactionReference) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			Operation operation = bank.getOperationBySourceAndReference(transactionSource, transactionReference);
			if (operation != null) {
				return operation;
			}
		}
		return null;
	}

	private static Bank getBankByCode(String code) {
		return FenixFramework.getDomainRoot().getBankSet().stream().filter(b -> b.getCode().equals(code)).findFirst()
				.orElse(null);
	}

	private static Account getAccountByIban(String iban) {
		Account account = FenixFramework.getDomainRoot().getBankSet().stream().filter(b -> b.getAccount(iban) != null)
				.map(b -> b.getAccount(iban)).findFirst().orElse(null);
		return account;
	}

}
