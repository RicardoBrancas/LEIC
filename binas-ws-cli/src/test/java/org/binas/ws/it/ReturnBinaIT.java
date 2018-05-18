package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static junit.framework.TestCase.fail;

public class ReturnBinaIT extends BaseIT {
	String email1 = "alice@A60.binas.org";
	String password1 = "LgzpKrs7F";
	String email2 = "binas@A60.binas.org";
	String password2 ="mRuFZfNu";
	String email3 ="charlie@A60.binas.org";
	String password3 = "Uj2cWtwk7";

	Key clientKey1;
	Key clientKey2;
	Key clientKey3;


	@Before
	@Override
	public void setup() {
		super.setup();
		try {
			clientKey1 = SecurityHelper.generateKeyFromPassword(password1);
			clientKey2 = SecurityHelper.generateKeyFromPassword(password2);
			clientKey3 = SecurityHelper.generateKeyFromPassword(password3);

			getTicket(email1, clientKey1);
			client.testInit(10);
			client.activateUser(email1);


			getTicket(email2, clientKey2);
			client.activateUser(email2);


			getTicket(email3, clientKey3);
			client.activateUser(email3);
			client.rentBina("1", email1);
			client.rentBina("2", email2);

		} catch (BadInit_Exception | InvalidEmail_Exception | EmailExists_Exception
				| UserNotExists_Exception | NoCredit_Exception | NoBinaAvail_Exception
				| AlreadyHasBina_Exception | InvalidStation_Exception |
				KerbyException|NoSuchAlgorithmException|BadTicketRequest_Exception |
				InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void success() throws UserNotExists_Exception, InvalidStation_Exception, NoBinaRented_Exception, FullStation_Exception, BadTicketRequest_Exception, KerbyException {
		getTicket(email2, clientKey2);
		int initialCredit = client.getCredit(email2);
		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();

		client.returnBina("1", email2);

		Assert.assertEquals(initialCredit + station1Prize, client.getCredit(email2));
		Assert.assertEquals(initialFreeDocks - 1, client.getInfoStation("1").getFreeDocks());
		Assert.assertEquals(initialAvailableBinas + 1, client.getInfoStation("1").getAvailableBinas());
		Assert.assertEquals(initialTotalGets, client.getInfoStation("1").getTotalGets());
		Assert.assertEquals(initialTotalReturns + 1, client.getInfoStation("1").getTotalReturns());
	}

	@Test
	public void nonExistentUser() throws InvalidStation_Exception, NoBinaRented_Exception, FullStation_Exception {
		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();

		try {
			client.returnBina("1", "another@email");
			fail();
		} catch (UserNotExists_Exception e) {
			Assert.assertEquals(initialFreeDocks, client.getInfoStation("1").getFreeDocks());
			Assert.assertEquals(initialAvailableBinas, client.getInfoStation("1").getAvailableBinas());
			Assert.assertEquals(initialTotalGets, client.getInfoStation("1").getTotalGets());
			Assert.assertEquals(initialTotalReturns, client.getInfoStation("1").getTotalReturns());
		}
	}

	@Test
	public void invalidStation() throws NoBinaRented_Exception, FullStation_Exception, UserNotExists_Exception, BadTicketRequest_Exception, KerbyException {
		getTicket(email1, clientKey1);

		int initialCredit = client.getCredit(email1);
		try {
			client.returnBina("5", email1);
			fail();
		} catch (InvalidStation_Exception e) {
			Assert.assertEquals(initialCredit, client.getCredit(email1));
		}
	}

	@Test
	public void noBinaRented() throws UserNotExists_Exception, InvalidStation_Exception, FullStation_Exception, BadTicketRequest_Exception, KerbyException {
		int initialFreeDocks = client.getInfoStation("1").getFreeDocks();
		int initialAvailableBinas = client.getInfoStation("1").getAvailableBinas();
		int initialTotalGets = client.getInfoStation("1").getTotalGets();
		int initialTotalReturns = client.getInfoStation("1").getTotalReturns();
		getTicket(email3, clientKey3);
		try {
			client.returnBina("1", email3);
		} catch (NoBinaRented_Exception e) {
			Assert.assertEquals(initialFreeDocks, client.getInfoStation("1").getFreeDocks());
			Assert.assertEquals(initialAvailableBinas, client.getInfoStation("1").getAvailableBinas());
			Assert.assertEquals(initialTotalGets, client.getInfoStation("1").getTotalGets());
			Assert.assertEquals(initialTotalReturns, client.getInfoStation("1").getTotalReturns());
		}
	}

	@Test
	public void fullStation() throws UserNotExists_Exception, InvalidStation_Exception, NoBinaRented_Exception, BadTicketRequest_Exception, KerbyException {
		getTicket(email1, clientKey1);
		int initialCredit = client.getCredit(email1);
		try {
			client.returnBina("3", email1);
			fail();
		} catch (FullStation_Exception e) {
			Assert.assertEquals(initialCredit, client.getCredit(email1));
		}
	}
}
