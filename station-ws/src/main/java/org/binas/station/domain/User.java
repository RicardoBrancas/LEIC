package org.binas.station.domain;

import org.binas.station.ws.UserView;

public class User {
	private String email;
	private int balance;
	private int tag;

	public User(String email, int balance, int tag) {
		this.email = email;
		this.balance = balance;
		this.tag = tag;
	}

	public String getEmail() {
		return email;
	}

	public int getBalance() {
		return this.balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getTag() {
		return this.tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public UserView asView() {
		UserView view = new UserView();
		view.setEmail(email);
		view.setBalance(balance);
		view.setTag(tag);
		return view;
	}

}
