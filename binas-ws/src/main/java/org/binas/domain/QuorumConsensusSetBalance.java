package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import java.util.List;

public class QuorumConsensusSetBalance extends QuorumConsensus {
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
	void quorumQuery(StationClient sc) {
		sc.setBalanceAsync(email, balance, tag, res -> addVote());
	}
}
