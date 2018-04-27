package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.List;

public abstract class QuorumConsensus {
	private int nVotes;
	private int currentVotes;
	private boolean isFinished;

	public synchronized void addVote() {
		currentVotes++;
		if (currentVotes >= nVotes) {
			isFinished = true;
		}
	}

	public boolean isFinished() {
		return isFinished;
	}

	QuorumConsensus(List<StationClient> scs, int nVotes) {
		this.currentVotes = 0;
		this.nVotes = nVotes;
		for (StationClient sc : scs) {
			quorumQuery(sc);
		}
	}

	abstract void quorumQuery(StationClient sc);
}

