package org.binas.station.ws.it;

import org.binas.station.ws.UserNotExists_Exception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.Holder;

public class GetBalanceIT extends BaseIT {

	private final String email = "me@binas";
	private Holder<Integer> balance;
	private Holder<Integer> tag;

	@Before
	public void setup() {
		client.setBalance(email, 10, 0);
		balance = new Holder<>(-1);
		tag = new Holder<>(-1);
	}

	@Test
	public void successGetBinasTest1() throws UserNotExists_Exception {
		client.getBalance(email, balance, tag);

		Assert.assertEquals(10, (int) balance.value);
	}

	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception {
		client.getBalance("another@binas", balance, tag);
	}
}
