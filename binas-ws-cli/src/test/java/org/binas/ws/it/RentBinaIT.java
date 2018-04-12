package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RentBinaIT extends BaseIT {
	private String userEmail = "want2rent@bina";

	@Before
	@Override
	public void setup() {
		super.setup();

		try {
			client.testInit(10);
			client.activateUser(userEmail);

		} catch (BadInit_Exception | InvalidEmail_Exception | EmailExists_Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void success() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {

		int initialCredit = client.getCredit(userEmail);
		client.rentBina("1", userEmail);

		Assert.assertTrue(initialCredit > client.getCredit(userEmail));
	}

	@Test(expected = NoBinaAvail_Exception.class)
	public void noMoreBinas() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception {
		int initialBinas = client.getInfoStation("1").getAvailableBinas();
		for (int i = 0; i < initialBinas + 1; ++i) {
			UserView currentUser = client.activateUser("user" + i + "@binas");
			client.rentBina("1", currentUser.getEmail());
		}
	}

	@Test(expected = UserNotExists_Exception.class)
	public void nonexistentUser() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		client.rentBina("1", "email.not.registered@binas");

	}

	@Test(expected = InvalidStation_Exception.class)
	public void nonexistentStation() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		client.rentBina("5", userEmail);

	}

	@Test(expected = AlreadyHasBina_Exception.class)
	public void alreadyHasBina() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		client.rentBina("1", userEmail);
		client.rentBina("2", userEmail);
	}

	@Test(expected = NoCredit_Exception.class)
	public void noCredit() throws UserNotExists_Exception, NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {
		client.testInit(0);
		client.activateUser("outro.email@binas");
		client.rentBina("1", "outro.email@binas");
	}
}
