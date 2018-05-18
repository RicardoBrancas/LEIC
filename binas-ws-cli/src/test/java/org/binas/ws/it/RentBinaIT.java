package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static junit.framework.TestCase.fail;

public class RentBinaIT extends BaseIT {

	@Before
	@Override
	public void setup() {
		super.setup();
		try {
			client.activateUser(email);
		} catch (EmailExists_Exception | InvalidEmail_Exception e) {
			throw new RuntimeException(e);
		}


	}

	@Test
	public void success() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception,
			AlreadyHasBina_Exception, UserNotExists_Exception {

		int initialCredit = client.getCredit(email);
		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();

		client.rentBina("1", email);

		Assert.assertEquals(initialCredit - 1, client.getCredit(email));
		Assert.assertEquals(initialFreeDocks + 1, client.getInfoStation("1").getFreeDocks());
		Assert.assertEquals(initialAvailableBinas - 1, client.getInfoStation("1").getAvailableBinas());
		Assert.assertEquals(initialTotalGets + 1, client.getInfoStation("1").getTotalGets());
		Assert.assertEquals(initialTotalReturns, client.getInfoStation("1").getTotalReturns());
	}

	@Test
	@Ignore
	public void noMoreBinas() throws NoCredit_Exception, InvalidStation_Exception, AlreadyHasBina_Exception,
			UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception, NoBinaAvail_Exception {

		int initialBinas = client.getInfoStation("1").getAvailableBinas();

		for (int i = 0; i < initialBinas; ++i) {
			UserView currentUser = client.activateUser("user" + i + "@binas");
			client.rentBina("1", currentUser.getEmail());
		}

		int creditBefore = client.getCredit(email);
		try {
			client.rentBina("1", email);
			fail();

		} catch (NoBinaAvail_Exception e) {
			Assert.assertEquals(0, client.getInfoStation("1").getAvailableBinas());
			Assert.assertEquals(initialBinas, client.getInfoStation("1").getTotalGets());
			Assert.assertEquals(0, client.getInfoStation("1").getTotalReturns());
			Assert.assertEquals(initialBinas, client.getInfoStation("1").getFreeDocks());
			Assert.assertEquals(creditBefore, client.getCredit(email));
		}
	}

	@Test
	@Ignore
	public void nonexistentUser() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception,
			AlreadyHasBina_Exception {
		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();
		try {
			client.rentBina("1", "email.not.registered@binas");
		} catch (UserNotExists_Exception e) {
			Assert.assertEquals(initialFreeDocks, client.getInfoStation("1").getFreeDocks());
		}

		Assert.assertEquals(initialFreeDocks, client.getInfoStation("1").getFreeDocks());
		Assert.assertEquals(initialAvailableBinas, client.getInfoStation("1").getAvailableBinas());
		Assert.assertEquals(initialTotalGets, client.getInfoStation("1").getTotalGets());
		Assert.assertEquals(initialTotalReturns, client.getInfoStation("1").getTotalReturns());

	}

	@Test
	public void nonexistentStation() throws NoBinaAvail_Exception, NoCredit_Exception,
			AlreadyHasBina_Exception, UserNotExists_Exception {
		int initialCredit = client.getCredit(email);
		try {
			client.rentBina("5", email);
			fail();
		} catch (InvalidStation_Exception e) {
			Assert.assertEquals(initialCredit, client.getCredit(email));
		}
	}

	@Test
	public void alreadyHasBina() throws NoBinaAvail_Exception, NoCredit_Exception, InvalidStation_Exception,
			UserNotExists_Exception, AlreadyHasBina_Exception {

		int initialCredit = client.getCredit(email);

		int initialFreeDocks1 = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas1 = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets1 = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns1 = client.getInfoStation("1").getTotalReturns();

		int initialFreeDocks2 = client.getInfoStation("2").getFreeDocks();
		int initialAvailableBinas2 = client.getInfoStation("2").getAvailableBinas();
		int initialTotalGets2 = client.getInfoStation("2").getTotalGets();
		int initialTotalReturns2 = client.getInfoStation("2").getTotalReturns();

		client.rentBina("1", email);

		try {
			client.rentBina("2", email);
			fail();
		} catch (AlreadyHasBina_Exception e) {
			Assert.assertEquals(initialCredit - 1, client.getCredit(email));

			Assert.assertEquals(initialFreeDocks1 + 1, client.getInfoStation("1").getFreeDocks());
			Assert.assertEquals(initialAvailableBinas1 - 1, client.getInfoStation("1").getAvailableBinas());
			Assert.assertEquals(initialTotalGets1 + 1, client.getInfoStation("1").getTotalGets());
			Assert.assertEquals(initialTotalReturns1, client.getInfoStation("1").getTotalReturns());

			Assert.assertEquals(initialFreeDocks2, client.getInfoStation("2").getFreeDocks());
			Assert.assertEquals(initialAvailableBinas2, client.getInfoStation("2").getAvailableBinas());
			Assert.assertEquals(initialTotalGets2, client.getInfoStation("2").getTotalGets());
			Assert.assertEquals(initialTotalReturns2, client.getInfoStation("2").getTotalReturns());
		}

	}

	@Test
	@Ignore
	public void noCredit() throws UserNotExists_Exception, NoBinaAvail_Exception, InvalidStation_Exception,
			AlreadyHasBina_Exception, EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {
		client.testInit(0);
		String otherEmail = "outro.email@binas";
		client.activateUser(otherEmail);

		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();


		try {
			client.rentBina("1", otherEmail);
		} catch (NoCredit_Exception e) {
			Assert.assertEquals(0, client.getCredit(otherEmail));
			Assert.assertEquals(initialFreeDocks, client.getInfoStation("1").getFreeDocks());
			Assert.assertEquals(initialAvailableBinas, client.getInfoStation("1").getAvailableBinas());
			Assert.assertEquals(initialTotalGets, client.getInfoStation("1").getTotalGets());
			Assert.assertEquals(initialTotalReturns, client.getInfoStation("1").getTotalReturns());
		}
	}
}
