package org.binas.ws.it;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ListStationsIT extends BaseIT {

	@Test
	public void success1() {
		CoordinatesView coordinates = new CoordinatesView();
		coordinates.setX(0);
		coordinates.setY(0);
		List<StationView> stations = client.listStations(1, coordinates);

		Assert.assertNotNull(stations);
		Assert.assertEquals(1, stations.size());
		Assert.assertEquals(new Integer(22), stations.get(0).getCoordinate().getX());
		Assert.assertEquals(new Integer(7),  stations.get(0).getCoordinate().getY());
	}

	public void success2() {
		CoordinatesView coordinates = new CoordinatesView();
		coordinates.setX(50);
		coordinates.setY(50);
		List<StationView> stations = client.listStations(1, coordinates);

		Assert.assertNotNull(stations);
		Assert.assertEquals(1, stations.size());
		Assert.assertEquals(new Integer(50), stations.get(0).getCoordinate().getX());
		Assert.assertEquals(new Integer(50),  stations.get(0).getCoordinate().getY());
	}

	public void success3() {
		CoordinatesView coordinates = new CoordinatesView();
		coordinates.setX(50);
		coordinates.setY(50);
		List<StationView> stations = client.listStations(3, coordinates);

		Assert.assertNotNull(stations);
		Assert.assertEquals(3, stations.size());
	}

}
