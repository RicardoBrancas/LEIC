package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class InfoIT extends BaseIT {

	private static final int X = 3;
	private static final int Y = 4;
	private static final int CAPACITY = 10;

	@Test
	public void infoTest() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, 0);
		StationView sv = client.getInfo();

		Assert.assertEquals(testProps.getProperty("ws.name"), sv.getId());
		Assert.assertEquals(X, sv.getCoordinate().getX());
		Assert.assertEquals(Y, sv.getCoordinate().getY());
		Assert.assertEquals(CAPACITY, sv.getCapacity());
		Assert.assertEquals(CAPACITY, sv.getAvailableBinas());
		Assert.assertEquals(0, sv.getFreeDocks());
		Assert.assertEquals(0, sv.getTotalGets());
		Assert.assertEquals(0, sv.getTotalReturns());
	}

	@After
	public void clear() {
		super.clear();
	}

}
