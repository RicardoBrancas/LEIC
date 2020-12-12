package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Operation extends Operation_Base {
	public static enum Type {
		DEPOSIT, WITHDRAW
	};

	public Operation(Type type, Account account, double value) {
		checkArguments(type, account, value);

		setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
		setType(type);
		setValue(value);
		setTime(DateTime.now());

		setAccount(account);

		setBank(account.getBank());
	}

	public void delete() {
		setBank(null);
		setAccount(null);

		deleteDomainObject();
	}

	private void checkArguments(Type type, Account account, double value) {
		if (type == null || account == null || value <= 0) {
			throw new BankException();
		}
	}

	public String revert() {
		setCancellation(getReference() + "_CANCEL");
		switch (getType()) {
		case DEPOSIT:
			return getAccount().withdraw(getValue()).getReference();
		case WITHDRAW:
			return getAccount().deposit(getValue()).getReference();
		default:
			throw new BankException();

		}

	}

}
