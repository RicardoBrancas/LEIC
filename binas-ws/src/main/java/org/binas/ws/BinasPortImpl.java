package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.domain.exception.*;
import org.binas.station.ws.cli.StationClient;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
@WebService(
		name = "BinasWebService",
		targetNamespace = "http://ws.binas.org/",
		serviceName = "BinasService",
		portName = "BinasPort",
		wsdlLocation = "binas.1_0.wsdl",
		endpointInterface = "org.binas.ws.BinasPortType"
)
public class BinasPortImpl implements BinasPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private BinasEndpointManager endpointManager;

	/**
	 * Constructor receives a reference to the endpoint manager.
	 */

	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		BinasManager.getInstance().setEndpointManager(endpointManager);
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		if (numberOfStations < 0 || coordinates == null)
			return new ArrayList<>();

		return BinasManager.getInstance().listStations().stream()
				.map(u -> BinasManager.convertStationView(u.getInfo()))
				.sorted(Comparator.comparingInt(v ->
						BinasManager.distanceBetween(coordinates, v.getCoordinate()))
				)
				.limit(numberOfStations)
				.collect(Collectors.toList());
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		try {
			return BinasManager.convertStationView(
					BinasManager.getInstance().getStation(stationId).getInfo());
		} catch (InvalidStationException e) {
			e.throwWSException();
		}
		return null; //should never happen
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		try {
			return BinasManager.getInstance().getUser(email).getCredit();
		} catch (UserNotExistsException e) {
			e.throwWSException();
		}
		return 0;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		try {
			User u = BinasManager.getInstance().createUser(email);
			return u.getView();
		} catch (EmailExistsException e) {
			e.throwWSException();
		} catch (InvalidEmailException e) {
			e.throwWSException();
		}
		return null; //should never happen
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		try {
			BinasManager.getInstance().rentBina(stationId, email);
		} catch (UserNotExistsException e) {
			e.throwWSException();
		} catch (AlreadyHasBinaException e) {
			e.throwWSException();
		} catch (InvalidStationException e) {
			e.throwWSException();
		} catch (NoCreditException e) {
			e.throwWSException();
		} catch (NoBinaAvailException e) {
			e.throwWSException();
		}
	}

	@Override
	public void returnBina(String stationId, String email) throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		try {
			BinasManager.getInstance().returnBina(stationId, email);
		} catch (UserNotExistsException e) {
			e.throwWSException();
		} catch (FullStationException e) {
			e.throwWSException();
		} catch (InvalidStationException e) {
			e.throwWSException();
		} catch (NoBinaRentedException e) {
			e.throwWSException();
		}
	}

	@Override
	public String testPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Station";

		// Build a string with a message to return.
		return "Hello " + inputMessage + " from " + wsName;
	}

	@Override
	public void testClear() {
		BinasManager.getInstance().clear();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) {
		try {
			StationClient stationClient = BinasManager.getInstance().getStation(stationId);
			stationClient.testInit(x, y, capacity, returnPrize);

		} catch (InvalidStationException | org.binas.station.ws.BadInit_Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void testInit(int userInitialPoints) {
		BinasManager.getInstance().setInitialCredit(userInitialPoints);
	}
}
