package org.binas.station.ws;

import org.binas.station.domain.Coordinates;
import org.binas.station.domain.Station;
import org.binas.station.domain.User;
import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;
import org.binas.station.domain.exception.UserNotExistsException;

import javax.jws.WebService;
import javax.xml.ws.Holder;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "org.binas.station.ws.StationPortType",
		wsdlLocation = "station.2_0.wsdl",
		name = "StationWebService",
		portName = "StationPort",
		targetNamespace = "http://ws.station.binas.org/",
		serviceName = "StationService"
)
public class StationPortImpl implements StationPortType {
	private static final Logger logger = Logger.getLogger(StationPortImpl.class.getName());

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private StationEndpointManager endpointManager;

	/**
	 * Constructor receives a reference to the endpoint manager.
	 */
	public StationPortImpl(StationEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	/**
	 * Retrieve information about station.
	 */
	@Override
	public StationView getInfo() {
		return buildStationView(Station.getInstance());
	}

	/**
	 * Return a bike to the station.
	 */
	@Override
	public int returnBina() throws NoSlotAvail_Exception {
		try {
			return Station.getInstance().returnBina();
		} catch (NoSlotAvailException e) {
			throwNoSlotAvail(e.getMessage());
		}
		return 0;
	}


	/**
	 * Take a bike from the station.
	 */
	@Override
	public void getBina() throws NoBinaAvail_Exception {
		try {
			Station.getInstance().getBina();
		} catch (NoBinaAvailException e) {
			throwNoBinaAvail(e.getMessage());
		}
	}

	@Override
	public void getBalance(String email, Holder<Integer> balance, Holder<Integer> tag) throws UserNotExists_Exception {
		logger.info(String.format("getBalance(%s) request received", email));
		//NOTE: holders are like C references. Used by jax-ws since we can't return multiple values
		try {
			Station.getInstance().getBalance(email, balance, tag);
		} catch (UserNotExistsException e) {
			e.throwWSException();
		}
	}

	@Override
	public synchronized void setBalance(String email, Integer balance, Integer tag) {
		logger.info(String.format("setBalance(%s, %d, %d) request received", email, balance, tag));
		Station.getInstance().setBalance(email, balance, tag);
	}

	@Override
	public List<UserView> getUsers() {
		logger.info("getUsers() request received");
		return Station.getInstance().getUsers().stream()
				.map(User::asView)
				.collect(Collectors.toList());
	}

	/**
	 * Diagnostic operation to check if service is running.
	 */
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

	/**
	 * Return all station variables to default values.
	 */
	@Override
	public void testClear() {
		Station.getInstance().reset();
	}

	/**
	 * Set station variables with specific values.
	 */
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws
			BadInit_Exception {
		try {
			Station.getInstance().init(x, y, capacity, returnPrize);
		} catch (BadInitException e) {
			throwBadInit("Invalid initialization values!");
		}
	}

	/**
	 * Helper to convert a domain station to a view.
	 */
	private StationView buildStationView(Station station) {
		StationView view = new StationView();
		view.setId(station.getId());
		view.setCoordinate(buildCoordinatesView(station.getCoordinates()));
		view.setCapacity(station.getMaxCapacity());
		view.setTotalGets(station.getTotalGets());
		view.setTotalReturns(station.getTotalReturns());
		view.setFreeDocks(station.getFreeDocks());
		view.setAvailableBinas(station.getAvailableBinas());
		return view;
	}

	/**
	 * Helper to convert a domain coordinates to a view.
	 */
	private CoordinatesView buildCoordinatesView(Coordinates coordinates) {
		CoordinatesView view = new CoordinatesView();
		view.setX(coordinates.getX());
		view.setY(coordinates.getY());
		return view;
	}

	/**
	 * Helper to throw a new NoBinaAvail exception.
	 */
	private void throwNoBinaAvail(final String message) throws
			NoBinaAvail_Exception {
		NoBinaAvail faultInfo = new NoBinaAvail();
		faultInfo.message = message;
		throw new NoBinaAvail_Exception(message, faultInfo);
	}

	/**
	 * Helper to throw a new NoSlotAvail exception.
	 */
	private void throwNoSlotAvail(final String message) throws
			NoSlotAvail_Exception {
		NoSlotAvail faultInfo = new NoSlotAvail();
		faultInfo.message = message;
		throw new NoSlotAvail_Exception(message, faultInfo);
	}

	/**
	 * Helper to throw a new BadInit exception.
	 */
	private void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.message = message;
		throw new BadInit_Exception(message, faultInfo);
	}

}
