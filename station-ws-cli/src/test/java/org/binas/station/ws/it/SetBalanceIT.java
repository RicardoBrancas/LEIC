package org.binas.station.ws.it;

import org.binas.station.ws.InvalidEmail_Exception;
import org.binas.station.ws.UserNotExists_Exception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.Holder;

public class SetBalanceIT extends BaseIT {

	private final String email = "me@binas";
	private Holder<Integer> tag;
	private Holder<Integer> balance;

	@Before
	public void setup() throws InvalidEmail_Exception {
		client.setBalance(email, 10, 0);
		balance = new Holder<>(-1);
		tag = new Holder<>(-1);
	}


	@Test
	public void success() throws UserNotExists_Exception, InvalidEmail_Exception {

		client.getBalance(email, balance, tag);

		Assert.assertEquals(10, (int) balance.value);
		Assert.assertEquals(0, (int) tag.value);

		client.setBalance(email, 15, 1);
		client.getBalance(email, balance, tag);

		Assert.assertEquals(15, (int) balance.value);
		Assert.assertEquals(1, (int) tag.value);
	}

	@Test
	public void lowerTag() throws UserNotExists_Exception, InvalidEmail_Exception {
		client.setBalance(email, 15, 3);
		client.setBalance(email, 20, 2);
		client.getBalance(email, balance, tag);

		Assert.assertEquals(15, (int) balance.value);
		Assert.assertEquals(3, (int) tag.value);
	}

	@Test
	public void repeatTag() throws UserNotExists_Exception, InvalidEmail_Exception {
		client.setBalance(email, 15, 0);
		client.getBalance(email, balance, tag);

		Assert.assertEquals(10, (int) balance.value);
		Assert.assertEquals(0, (int) tag.value);
	}
}
