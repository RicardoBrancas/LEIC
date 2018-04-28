package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.Collection;
import java.util.List;

//TODO should *maybe* implement Runnable in the future
public abstract class QuorumConsensus {
	private int nVotes;
	private int currentVotes;
	private boolean isFinished;
	private Collection<StationClient> stationClients;

	QuorumConsensus(List<StationClient> scs, int nVotes) {
		this.currentVotes = 0;
		this.nVotes = nVotes;
		this.stationClients = scs;
	}

	public synchronized void addVote() {
		currentVotes++;
		if (currentVotes >= nVotes) {
			isFinished = true;
		}
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void run() {
		for (StationClient sc : stationClients) {
			quorumQuery(sc);
		}
	}

	abstract void quorumQuery(StationClient sc);
}

