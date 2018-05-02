package org.binas.domain;

import org.binas.domain.exception.QuorumNotReachedException;
import org.binas.station.ws.cli.StationClient;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Abstract class to implement the Quorum Consensus protocol using a Template Method
 *
 * @param <T> The reply type
 */
public abstract class QuorumConsensus<T> {
	private static final Logger logger = Logger.getLogger(QuorumConsensus.class.getName());

	private final int nVotes;
	/**
	 * The result of this quorum consensus, if finished. Undefined otherwise.
	 */
	protected T result = null;
	private int currentVotes;
	private boolean isFinished;
	private Collection<StationClient> stationClients;
	private Set<Future<?>> futures;

	QuorumConsensus(List<StationClient> stationClients, int nVotes) {
		this.currentVotes = 0;
		this.nVotes = nVotes;
		this.stationClients = stationClients;
		futures = new HashSet<>();
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
			futures.add(quorumQuery(sc));
		}
		logger.info(String.format("All %d requests sent", stationClients.size()));
	}


	/**
	 * Waits if necessary for the computation to complete, and then retrieves its result.
	 *
	 * @return the computed result
	 * @throws QuorumNotReachedException if it has become impossible to reach quorum for this request
	 * @throws InterruptedException      if the current thread was interrupted while waiting
	 */

	public T get() throws QuorumNotReachedException {
		boolean stillWaiting = false;

		for (Future<?> future : futures) {
			if (!future.isDone()) {
				stillWaiting = true;
				break;
			}
		}

		while (!isFinished() && stillWaiting) {
			stillWaiting = false;

			for (Future<?> future : futures) {
				if (!future.isDone()) {
					stillWaiting = true;
					break;
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("A thread was interrupted while waiting for a response.");
				break;
			}
		}

		if (!isFinished())
			throw new QuorumNotReachedException();

		return result;
	}

	/**
	 * Implements the query.
	 * <p>
	 * It is required that when {@link #isFinished()} returns true,
	 * {@link #result} must contain the result of this request.
	 *
	 * @param stationClient
	 */
	abstract Future<?> quorumQuery(StationClient stationClient);
}

