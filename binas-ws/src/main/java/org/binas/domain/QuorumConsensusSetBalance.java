package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Uses Quorum Consensus protocol to set the balance for a user
 */
public class QuorumConsensusSetBalance extends QuorumConsensus<Void> {
	private static final Logger logger = Logger.getLogger(QuorumConsensusGetBalance.class.getName());

	private String email;
	private Integer balance;
	private Integer tag;

	public QuorumConsensusSetBalance(List<StationClient> scs, int nVotes, String email, Integer balance, Integer tag) {
		super(scs, nVotes);
		this.email = email;
		this.balance = balance;
		this.tag = tag;
	}

	/**
	 * Implements the query to set the balance for the user
	 *
	 * @param sc
	 * @return
	 */
	@Override
	Future<?> quorumQuery(StationClient sc) {
		return sc.setBalanceAsync(email, balance, tag, res -> {
			try {
				res.get();
				addVote();
			} catch (InterruptedException e) {
				logger.warning("Caught interrupted exception: " + e.getCause());
			} catch (ExecutionException e) {
				logger.warning("Caught execution exception: " + e.getCause());
			}
		});
	}
}
