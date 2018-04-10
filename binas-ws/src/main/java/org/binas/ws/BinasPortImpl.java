package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.domain.exception.*;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import javax.jws.WebService;
import java.util.Collection;
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
	/*
	 * TODO: Many things done here maybe should be done in BinasManager
	 * /
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
		CoordinatesView retCv = new CoordinatesView();
		retCv.setX(cv.getX());
		retCv.setY(cv.getY());
		return retCv;
	}

	private StationView convertStationView(org.binas.station.ws.StationView sv) {

		StationView retSv = new StationView();
		retSv.setId(sv.getId());
		retSv.setCoordinate(convertCoordinatesView(sv.getCoordinate()));
		retSv.setCapacity(sv.getCapacity());
		retSv.setTotalGets(sv.getTotalGets());
		retSv.setTotalReturns(sv.getTotalReturns());
		retSv.setAvailableBinas(sv.getAvailableBinas());
		retSv.setFreeDocks(sv.getFreeDocks());
		return retSv;
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		/*TODO
		 * Make this readable
		 * Verify if sort should be done HERE
		 * Verify if sort metric should be Manhatan distance
		 * */
		return BinasManager.getInstance().getStations().stream()
				.map(u -> convertStationView(u.getInfo()))
				.sorted(Comparator.comparingInt(v ->
						Math.abs(v.getCoordinate().getX() + v.getCoordinate().getY()
								- coordinates.getX() - coordinates.getY()))
				)
				.limit(numberOfStations)
				.collect(Collectors.toList());
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
		User u = null;

		try {
			u = BinasManager.getInstance().createUser(email);
		} catch (EmailExistsException e) {
			e.throwWSException();
		} catch (InvalidEmailException e) {
			e.throwWSException();
		}

		return u.getView();
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
