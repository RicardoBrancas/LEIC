package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

//TODO should *maybe* implement Runnable/RunnableFuture
public abstract class QuorumConsensus<T> {
	private static final Logger logger = Logger.getLogger(QuorumConsensus.class.getName());

	private final int nVotes;
	private int currentVotes;
	private boolean isFinished;
	private Collection<StationClient> stationClients;

	QuorumConsensus(List<StationClient> scs, int nVotes) {
		this.currentVotes = 0;
		this.nVotes = nVotes;
		this.stationClients = scs;
	}

	public synchronized void addVote() {
		logger.info("Adding vote...");
		currentVotes++;
		if (currentVotes >= nVotes) {
			isFinished = true;
		}
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void run() {
		logger.info("Starting quorum for " + this.getClass().getSimpleName());
		for (StationClient sc : stationClients) {
			quorumQuery(sc);
		}
		logger.info(String.format("All %d requests sent", stationClients.size()));
	}

	//uniform way to get values out of a quorum consensus
	public abstract T get() throws InterruptedException;

	abstract void quorumQuery(StationClient sc);
}

