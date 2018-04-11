package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.domain.exception.*;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

import javax.jws.WebService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
	/*
	 * TODO: Many things done here maybe should be done in BinasManager
	 *
	 */
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
	}

	private CoordinatesView convertCoordinatesView(org.binas.station.ws.CoordinatesView cv) {
		CoordinatesView coordinatesView = new CoordinatesView();
		coordinatesView.setX(cv.getX());
		coordinatesView.setY(cv.getY());
		return coordinatesView;
	}

	private StationView convertStationView(org.binas.station.ws.StationView sv) {
		StationView stationView = new StationView();
		stationView.setId(sv.getId());
		stationView.setCoordinate(convertCoordinatesView(sv.getCoordinate()));
		stationView.setCapacity(sv.getCapacity());
		stationView.setTotalGets(sv.getTotalGets());
		stationView.setTotalReturns(sv.getTotalReturns());
		stationView.setAvailableBinas(sv.getAvailableBinas());
		stationView.setFreeDocks(sv.getFreeDocks());
		return stationView;
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		//TODO should probably be moved to BinasManager

		try {
			return endpointManager.getUddiNaming().listRecords("A60_Station%").stream()
					.map(uddiRecord -> {
						try {
							return new StationClient(uddiRecord.getUrl());
						} catch (StationClientException e) {
							e.printStackTrace(); //TODO catch exceptions properly
							return null;
						}
					})
					.filter(Objects::nonNull)
					.map(u -> convertStationView(u.getInfo()))
					.sorted(Comparator.comparingInt(v ->
							Math.abs(v.getCoordinate().getX() + v.getCoordinate().getY()
									- coordinates.getX() - coordinates.getY()))
					)
					.limit(numberOfStations)
					.collect(Collectors.toList());

		} catch (UDDINamingException e) {
			e.printStackTrace(); //TODO catch exceptions properly
		}

		return null;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		org.binas.station.ws.StationView sv = null;
		try {
			sv = BinasManager.getInstance().getStation(stationId).getInfo();
		} catch (InvalidStationException e) {
			e.throwWSException();
		}
		return convertStationView(sv);
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		int credit = 0;
		try {
			credit = BinasManager.getInstance().getUser(email).getCredit();
		} catch (UserNotExistsException e) {
			e.throwWSException();
		}
		return credit;
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
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	@Override
	public void testClear() {
		BinasManager.getInstance().clear();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		//TODO
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		//TODO
	}
}
