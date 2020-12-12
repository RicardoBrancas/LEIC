package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Uses Quorum Consensus protocol to get the current balance of a user
 */
public class QuorumConsensusGetBalance extends QuorumConsensus<User.Replica> {
	private static final Logger logger = Logger.getLogger(QuorumConsensusGetBalance.class.getName());

	private final String email;

	public QuorumConsensusGetBalance(List<StationClient> stationClients, int nVotes, String email) {
		super(stationClients, nVotes);
		this.email = email;
		result = new User.Replica();
		result.setEmail(email);
	}

	/**
	 * Implements the query to get the balance for the user
	 *
	 * @param stationClient
	 * @return
	 */
	@Override
	Future<?> quorumQuery(StationClient stationClient) {
		return stationClient.getBalanceAsync(email, res -> {
			try {
				int newBalance = res.get().getGetBalance();
				int newTag = res.get().getTag();
				synchronized (result) {
					if (newTag > result.getTag()) {
						result.setTag(newTag);
						result.setBalance(newBalance);
					}
				}
				addVote();
			} catch (InterruptedException e) {
				logger.warning("Caught interrupted exception: " + e.getCause());
			} catch (ExecutionException e) {
				logger.warning("Caught execution exception: " + e.getCause());
			}
		});
	}
}
