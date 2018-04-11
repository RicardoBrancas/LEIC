package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RentBinaIT extends BaseIT {
	private UserView user;
	//TODO find a way to setCredit on user
/*
	@Before
	public void setup() throws Exception {
		user = client.activateUser("want2rent@bina");
	}

	@Test
	public void success() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		System.out.println(user.getCredit());
		System.out.println(user.getEmail());

		client.rentBina("1", user.getEmail());

		System.out.println(user.getCredit());
	}

	@Test
	public void noMoreBinas() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		while(client.getInfoStation("1").getAvailableBinas() > 0)
			client.rentBina("1", user.getEmail());

		client.rentBina("1", user.getEmail());
	}

	@Test
	public void unexistentUser() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception, UserNotExists_Exception {
		client.rentBina("1", "email.not.registered@binas");

	}

	//TODO unexistentUser
	//TODO unexistenStation
	//TODO alreadyHasBina
	//TODO noMoreBinas
	//TODO noCredit

	@After
	public void tearDown() {
		client.testClear();
	}
*/
}
