package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Uses Quorum Consensus protocol to set the balance for a user
 */
public class QuorumConsensusSetBalance extends QuorumConsensus<Void> {
	private String email;
	private Integer balance;
	private Integer tag;

	public QuorumConsensusSetBalance(List<StationClient> scs, int nVotes, String email, Integer balance, Integer tag) {
		super(scs, nVotes);
		this.email = email;
		this.balance = balance;
		this.tag = tag;
	}

	@Override
	Future<?> quorumQuery(StationClient sc) {
		return sc.setBalanceAsync(email, balance, tag, res -> addVote());
	}
}
