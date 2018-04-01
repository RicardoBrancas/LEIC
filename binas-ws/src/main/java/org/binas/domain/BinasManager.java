package org.binas.domain;

import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;

import java.util.HashMap;
import java.util.Map;

public class BinasManager {

	private final Map<String, User> users = new HashMap<>();

	private BinasManager() {
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public User createUser(String email) throws EmailExistsException, InvalidEmailException {
		if (users.containsKey(email))
			throw new EmailExistsException();

		User u = new User(email);

		users.put(email, u);

		return u;
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}


	// TODO

}
