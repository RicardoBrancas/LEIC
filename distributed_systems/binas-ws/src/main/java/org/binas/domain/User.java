package org.binas.domain;

import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.QuorumNotReachedException;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.UserView;

import java.util.List;

public class User {

	private final static String EMAIL_FORMAT = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)*";
	private int tag = -1;
	private String email;
	private boolean hasBina;
	private int credit;

	public User(String email, int credit) throws InvalidEmailException {
		checkParams(email);

		this.email = email;
		this.credit = credit;
	}

	private void checkParams(String email) throws InvalidEmailException {
		if (email == null) {
			throw new InvalidEmailException("Email is null");
		} else if (email.isEmpty()) {
			throw new InvalidEmailException("Email is empty");
		} else if (!email.matches(EMAIL_FORMAT)) {
			throw new InvalidEmailException("Email doesn't match the format");
		}
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

	public int getTag() {
		return tag;
	}

	void setTag(int tag) {
		this.tag = tag;
	}

	/**
	 * Uses the Quorum Consensus protocol to set the credit for the user.
	 * @param credit
	 * @throws QuorumNotReachedException
	 */
	public synchronized void setCredit(int credit) throws QuorumNotReachedException{
		List<StationClient> stations = BinasManager.getInstance().listStations();
		//number of votes necessary
		int nQC = BinasManager.getInstance().getQC();
		QuorumConsensus qc = new QuorumConsensusSetBalance(stations, nQC, getEmail(), credit, ++tag);
		qc.run();

		qc.get();
		this.credit = credit;
	}

	public UserView getView() {
		UserView userView = new UserView();
		userView.setEmail(email);
		userView.setHasBina(hasBina);
		userView.setCredit(credit);

		return userView;
	}

	public synchronized void increaseCredit(int credit) throws QuorumNotReachedException {
		setCredit(getCredit() + credit);
	}

	public synchronized void decreaseCredit(int credit) throws QuorumNotReachedException {
		setCredit(getCredit() - credit);
	}

	/**
	 * Auxiliary class to export information about a user
	 */
	public static class Replica {

		private String email;
		private int balance;
		private int tag = -1;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public int getBalance() {
			return balance;
		}

		public void setBalance(int balance) {
			this.balance = balance;
		}

		public int getTag() {
			return tag;
		}

		public void setTag(int tag) {
			this.tag = tag;
		}
	}
}
