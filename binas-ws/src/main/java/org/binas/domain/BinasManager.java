package org.binas.domain;

import org.binas.domain.exception.*;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import org.binas.ws.BinasEndpointManager;
import org.binas.ws.CoordinatesView;
import org.binas.ws.InvalidEmail;
import org.binas.ws.StationView;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BinasManager {

	private final AtomicInteger initialCredit;

	private final Map<String, User> users = new ConcurrentHashMap<>();

	private BinasEndpointManager endpointManager;

	private BinasManager() {
		initialCredit = new AtomicInteger(10);
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void setInitialCredit(int initialCredit) {
		this.initialCredit.set(initialCredit);
	}

	public static CoordinatesView convertCoordinatesView(org.binas.station.ws.CoordinatesView cv) {
		CoordinatesView coordinatesView = new CoordinatesView();
		coordinatesView.setX(cv.getX());
		coordinatesView.setY(cv.getY());
		return coordinatesView;
	}

	public static StationView convertStationView(org.binas.station.ws.StationView sv) {
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

	public static int distanceBetween(CoordinatesView c1, CoordinatesView c2) {
		return Math.abs(c1.getX() - c2.getX() + c1.getY() - c2.getY());
	}

	public synchronized List<StationClient> listStations() {
		try {
			return endpointManager.getUddiNaming().listRecords("A60_Station%").stream()
					.map(uddiRecord -> {
						try {
							return new StationClient(uddiRecord.getUrl());
						} catch (StationClientException e) {
							//connection to station failed. ignore the station
							return null; //nulls will be filtered out next
						}
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

		} catch (UDDINamingException e) {
			System.out.println("Error with UDDI: e.getMessage()");
			e.printStackTrace();
		}
		return new ArrayList<StationClient>(); //should never happen
	}

	public synchronized StationClient getStation(String stationId) throws InvalidStationException {
		try {
			UDDIRecord record = endpointManager.getUddiNaming().lookupRecord("A60_Station" + stationId);
			if (record == null)
				throw new InvalidStationException("Record is null");

			return new StationClient(record.getUrl());

		} catch (UDDINamingException | StationClientException e) {
			throw new InvalidStationException();
		}
	}

	public synchronized void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException, NoBinaAvailException, NoCreditException, UserNotExistsException {
		User u = getUser(email);

		if (u.hasBina())
			throw new AlreadyHasBinaException("User " + u.getEmail() + " is already renting a BINA.");

		if (u.getCredit() <= 0)
			throw new NoCreditException("User " + u.getEmail() + " is out of credit");

		StationClient s = getStation(stationId);
		try {
			s.getBina();
			u.setHasBina(true);
			u.decreaseCredit(1);
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			throw new NoBinaAvailException("No Binas available at station " + s.getInfo().getId());
		}

	}

	public synchronized void returnBina(String stationId, String email) throws FullStationException, InvalidStationException, NoBinaRentedException, UserNotExistsException {
		User u = getUser(email);

		if (!u.hasBina())
			throw new NoBinaRentedException("User " + u.getEmail() + " has no Bina to return.");

		StationClient s = getStation(stationId);
		try {
			u.increaseCredit(s.returnBina());
			u.setHasBina(false);
		} catch (NoSlotAvail_Exception e) {
			throw new FullStationException("Station " + s.getInfo().getId() + " has no free docks.");
		}
	}

	public synchronized User getUser(String email) throws UserNotExistsException {
		if (email == null) {
			throw new UserNotExistsException("Email is null");
		} else if (!users.containsKey(email)) {
			throw new UserNotExistsException("No user with email " + email);
		}

		return users.get(email);
	}

	public synchronized User createUser(String email) throws EmailExistsException, InvalidEmailException {
		if (email == null)
			throw new InvalidEmailException("Email is null");

		if (users.containsKey(email))
			throw new EmailExistsException(email + " is already registered.");

		User u = new User(email, this.initialCredit.get());

		users.put(email, u);

		return u;
	}

	public void clear() {
		users.clear();
	}

	public synchronized void setEndpointManager(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}
}
