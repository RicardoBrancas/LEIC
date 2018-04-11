package org.binas.domain;

import org.binas.domain.exception.InvalidEmailException;
import org.binas.ws.UserView;

public class User {

	private final static String EMAIL_FORMAT = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)*";

	private String email;
	private boolean hasBina;
	private int credit;

	public User(String email) throws InvalidEmailException {
		checkParams(email);

		this.email = email;
	}

	private void checkParams(String email) throws InvalidEmailException {
		if (email == null || email.isEmpty() || !email.matches(EMAIL_FORMAT))
			throw new InvalidEmailException();
	}

	public String getEmail() {
		return email;
	}

	public boolean hasBina() {
		return hasBina;
	}

	public void setHasBina(boolean hasBina) {
		this.hasBina = hasBina;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public UserView getView() {
		UserView v = new UserView();
		v.setEmail(email);
		v.setHasBina(hasBina);
		v.setCredit(credit);

		return v;
	}

	public synchronized void increaseCredit(int credit	) {
		setCredit(getCredit()+credit);
	}
}
