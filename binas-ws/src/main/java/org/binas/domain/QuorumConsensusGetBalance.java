package org.binas.domain;

import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.cli.StationClient;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Holder;
import javax.xml.ws.Response;
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

		class AsyncHandlerWithQC implements AsyncHandler<GetBalanceResponse> {
			QuorumConsensusGetBalance qc;

			AsyncHandlerWithQC(QuorumConsensusGetBalance qc) {
				super();
				this.qc = qc;
			}

			@Override
			public void handleResponse(Response<GetBalanceResponse> res) {

				try {
					int newBalance = res.get().getGetBalance();
					int newTag = res.get().getTag();
					if (newTag > this.qc.tag.value) {
						this.qc.tag.value = newTag;
						this.qc.balance.value = newBalance;
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
			}

		}

		//can this be replaced with a lambda?
		sc.getBalanceAsync(email, new AsyncHandlerWithQC(this));
	}
}
