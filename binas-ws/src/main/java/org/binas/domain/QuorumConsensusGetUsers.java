package org.binas.domain;

import org.binas.domain.exception.InvalidEmailException;
import org.binas.station.ws.UserView;
import org.binas.station.ws.cli.StationClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class QuorumConsensusGetUsers extends QuorumConsensus<ConcurrentMap<String, User>> {

	private ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
	private BinasManager binasInstance;

	QuorumConsensusGetUsers(List<StationClient> scs, int nVotes, BinasManager binasInstance) {
		super(scs, nVotes);
		this.binasInstance = binasInstance;
	}

	@Override
	public ConcurrentMap<String, User> get() throws InterruptedException {
		while (!isFinished()) Thread.sleep(100);
		return users;
	}

	@Override
	void quorumQuery(StationClient sc) {
		sc.getUsersAsync(res -> {
			try {

				for (UserView userView : res.get().getUsers()) {
					if(users.containsKey(userView.getEmail())) {
						User currentUser = users.get(userView.getEmail());


						if(userView.getTag() > currentUser.getTag()) {
							currentUser._setCredit(userView.getBalance());
							currentUser.setTag(userView.getTag());
						}
					} else {
						users.put(userView.getEmail(), new User(userView.getEmail(), userView.getBalance(), binasInstance));
					}
				}

				addVote();
			} catch (InterruptedException | ExecutionException | InvalidEmailException e) {
				e.printStackTrace(); //TODO treat exceptions properly
			}
		});
	}
}
