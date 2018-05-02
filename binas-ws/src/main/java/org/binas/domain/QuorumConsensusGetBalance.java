package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import javax.xml.ws.Holder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Uses Quorum Consensus protocol to get the current balance of a user
 */
public class QuorumConsensusGetBalance extends QuorumConsensus<User.Replica> {

	private final String email;

	public QuorumConsensusGetBalance(List<StationClient> stationClients, int nVotes, String email) {
		super(stationClients, nVotes);
		this.email = email;
		result = new User.Replica();
		result.setEmail(email);
	}

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
