package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import javax.xml.ws.Holder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuorumConsensusGetBalance extends QuorumConsensus {
	private String email;
	private Holder<Integer> balance;
	private Holder<Integer> tag;

	public QuorumConsensusGetBalance(List<StationClient> scs, int nVotes, String email, Holder<Integer> balance, Holder<Integer> tag) {
		super(scs, nVotes);
		this.email = email;
		this.balance = balance;
		this.tag = tag;
		//Im not sure if this works like this
		this.balance.value = 0;
		this.tag.value = -1;
	}


	@Override
	void quorumQuery(StationClient sc) {
		sc.getBalanceAsync(email, res -> {
			try {
				int newBalance = res.get().getGetBalance();
				int newTag = res.get().getTag();
				if (newTag > tag.value) {
					tag.value = newTag;
					balance.value = newBalance;
				}
				addVote();
			} catch (InterruptedException e) {
				System.out.println("Caught interrupted exception.");
				System.out.print("Cause: ");
				System.out.println(e.getCause());
			} catch (ExecutionException e) {
				System.out.println("Caught execution exception.");
				System.out.print("Cause: ");
				System.out.println(e.getCause());
			}
		});
	}
}
