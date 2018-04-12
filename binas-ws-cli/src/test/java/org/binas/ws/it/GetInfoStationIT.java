package org.binas.ws.it;

import com.sun.xml.ws.fault.ServerSOAPFaultException;
import org.binas.ws.*;
import org.junit.Assert;
import org.junit.Test;

public class GetInfoStationIT extends BaseIT {

	@Test
	public void success1() throws InvalidStation_Exception {
		StationView stationView = client.getInfoStation("1");

		Assert.assertEquals(stationName + "1", stationView.getId());
		Assert.assertEquals(station1X, (int) stationView.getCoordinate().getX());
		Assert.assertEquals(station1Y, (int) stationView.getCoordinate().getY());
		Assert.assertEquals(station1Capacity, stationView.getCapacity());
	}

	@Test
	public void success2() throws InvalidStation_Exception {
		StationView stationView = client.getInfoStation("2");

		Assert.assertEquals(stationName + "2", stationView.getId());
		Assert.assertEquals(station2X, (int) stationView.getCoordinate().getX());
		Assert.assertEquals(station2Y, (int) stationView.getCoordinate().getY());
		Assert.assertEquals(station2Capacity, stationView.getCapacity());
	}

	@Test
	public void success3() throws InvalidStation_Exception {
		StationView stationView = client.getInfoStation("3");

		Assert.assertEquals(stationName + "3", stationView.getId());
		Assert.assertEquals(station3X, (int) stationView.getCoordinate().getX());
		Assert.assertEquals(station3Y, (int) stationView.getCoordinate().getY());
		Assert.assertEquals(station3Capacity, stationView.getCapacity());
	}

	@Test(expected = InvalidStation_Exception.class)
	public void nonexistentStation() throws InvalidStation_Exception {
		client.getInfoStation("4");
	}

	@Test(expected = InvalidStation_Exception.class)
	public void nullStation() throws InvalidStation_Exception {
		StationView stationView = client.getInfoStation(null);

	}

}
