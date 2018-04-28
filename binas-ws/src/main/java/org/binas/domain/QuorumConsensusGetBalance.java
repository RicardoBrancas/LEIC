package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import javax.xml.ws.Holder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuorumConsensusGetBalance extends QuorumConsensus<Void> {
	private String email;
	private Holder<Integer> balance; //Maybe don't use jax-ws holders here?
	private Holder<Integer> tag;

	public QuorumConsensusGetBalance(List<StationClient> scs, int nVotes, String email, Holder<Integer> balance, Holder<Integer> tag) {
		super(scs, nVotes);
		this.email = email;
		this.balance = balance;
		this.tag = tag;
		//Im not sure if this works like this
		//FIXME ricardo: I don't think this is needed
		this.balance.value = 0;
		this.tag.value = -1;
	}


	@Override
	public Void get() throws InterruptedException {
		while (!isFinished()) Thread.sleep(100);
		return null;
	}

	@Override
	void quorumQuery(StationClient sc) {
		sc.getBalanceAsync(email, res -> {
			try {
				int newBalance = res.get().getGetBalance();
				int newTag = res.get().getTag();
				if (newTag > tag.value) {
					tag.value = newTag; //FIXME concurrent access to variable
					balance.value = newBalance; //FIXME concurrent access to variable
				}
				addVote();
			} catch (InterruptedException e) { //TODO ricardo: eu sei que isto é o que está nos labs, mas isto é fazer printStackTrace à mão xD
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
