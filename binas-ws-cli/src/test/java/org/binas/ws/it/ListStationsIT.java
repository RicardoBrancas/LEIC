package org.binas.ws.it;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ListStationsIT extends BaseIT {

	@Test
	public void success() {
		CoordinatesView coordinates = new CoordinatesView();
		coordinates.setX(0);
		coordinates.setY(0);
		List<StationView> stations = client.listStations(3, coordinates);

		Assert.assertNotNull(stations);
	}

}
