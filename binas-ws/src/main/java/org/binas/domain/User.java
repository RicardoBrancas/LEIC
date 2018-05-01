package org.binas.domain;

import org.binas.domain.exception.InvalidEmailException;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.UserView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class User {

	private final static String EMAIL_FORMAT = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)*";
	private int tag = -1;
	private String email;
	private boolean hasBina;
	private int credit;
	private BinasManager binasInstance;

	public User(String email, int credit, BinasManager binasInstance) throws InvalidEmailException {
		checkParams(email);

		this.email = email;
		this.binasInstance = binasInstance;
		this.credit = credit;
	}

	//TODO check all parameters
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

	//TODO: confirm this is synchronized at every call
	public int getCredit() {
		return credit;
//		//number of votes necessary
//		int nQC = binasInstance.getQC();
//
//		List<StationClient> scs = binasInstance.listStations();
//
//
//		QuorumConsensus<User.Replica> qc = new QuorumConsensusGetBalance(scs, nQC, getEmail());
//		qc.run();
//		try {
//			User.Replica replica = qc.get(); //TODO catch exceptions properly
//
//			if (tag < replica.getTag()) {
//				tag = replica.getTag();
//				credit = replica.getBalance();
//			}
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (QuorumNotReachedException e) {
//			e.printStackTrace();
//		}
//
//		return credit;
	}

	public void setCredit(int credit) {
		List<StationClient> stations = binasInstance.listStations();
		//number of votes necessary
		int nQC = binasInstance.getQC();
		QuorumConsensus qc = new QuorumConsensusSetBalance(stations, nQC, getEmail(), credit, ++tag);
		qc.run();

		try {
			qc.get(); //TODO catch exceptions properly
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (QuorumNotReachedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		this.credit = credit;
	}

	void _setCredit(int credit) {
		this.credit = credit;
	}

	public int getTag() {
		return tag;
	}

	void setTag(int tag) {
		this.tag = tag;
	}

	public UserView getView() {
		UserView v = new UserView();
		v.setEmail(email);
		v.setHasBina(hasBina);
		v.setCredit(credit);

		return v;
	}

	public synchronized void increaseCredit(int credit) {
		setCredit(getCredit() + credit);
	}

	public synchronized void decreaseCredit(int credit) {
		setCredit(getCredit() - credit);
	}

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
