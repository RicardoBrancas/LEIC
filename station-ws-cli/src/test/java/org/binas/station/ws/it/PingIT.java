package org.binas.station.ws.it;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class PingIT extends BaseIT {

	@Test
	public void pingNormalTest() {
		Assert.assertEquals("Hello azul from " + testProps.getProperty("ws.name"), client.testPing("azul"));
	}

	@Test
	public void pingEmptyTest() {
		Assert.assertEquals("Hello friend from " + testProps.getProperty("ws.name"), client.testPing(""));
	}

	@Test
	public void pingNullTest() {
		Assert.assertEquals("Hello friend from " + testProps.getProperty("ws.name"), client.testPing(null));
	}

	@Override
	@After
	public void clear() {
		super.clear();
	}

}
