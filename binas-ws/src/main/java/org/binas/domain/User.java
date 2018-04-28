package org.binas.domain;

import org.binas.domain.exception.InvalidEmailException;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.UserView;

import javax.xml.ws.Holder;
import java.util.List;

public class User {

	private final static String EMAIL_FORMAT = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)*";
	private int mID = -1;
	private String email;
	private boolean hasBina;
	private Integer credit;
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
		if (credit != null)
			return credit;

		//number of votes necessary
		int nQC = binasInstance.getQC();

		List<StationClient> scs = binasInstance.listStations();

		Holder<Integer> newBalance = new Holder<>();
		Holder<Integer> newMID = new Holder<>();
		QuorumConsensus qc = new QuorumConsensusGetBalance(scs, nQC, getEmail(), newBalance, newMID);
		qc.run();

		while (!qc.isFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//TODO: Handle exception
				e.printStackTrace();
			}
		}
		int newMIDVal = newMID.value;
		int newBalanceVal = newBalance.value;
		if (mID < newMIDVal) {
			mID = newMIDVal;
			credit = newBalanceVal;
		}
		return credit;
	}

	void _setCredit(int credit) {
		this.credit = credit;
	}

	public void setCredit(int credit) {
		List<StationClient> scs = binasInstance.listStations();
		//number of votes necessary
		int nQC = binasInstance.getQC();
		QuorumConsensus qc = new QuorumConsensusSetBalance(scs, nQC, getEmail(), credit, mID);
		qc.run();

		while (!qc.isFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//TODO handle exception
				e.printStackTrace();
			}
		}

		//this increment means
		//the user's tag is always
		//one step ahead of the replica tag...
		//maybe they should both be the same...
		mID++;

		this.credit = credit;
	}

	void setTag(int tag) {
		mID = tag;
	}

	public int getTag() {
		return mID;
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
}
