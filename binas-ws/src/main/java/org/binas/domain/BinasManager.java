package org.binas.domain;

import org.binas.domain.exception.*;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import org.binas.ws.BinasEndpointManager;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.util.*;
import java.util.stream.Collectors;

public class BinasManager {

	private final Map<String, User> users = new HashMap<>();

	private BinasEndpointManager endpointManager;

	private BinasManager() {
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
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

	public List<StationClient> listStations() {
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
			e.printStackTrace(); //TODO catch exceptions properly
		}
		return null; //should never happen
	}

	public StationClient getStation(String stationId) throws InvalidStationException {
		try {
			UDDIRecord record = endpointManager.getUddiNaming().lookupRecord("A60_Station" + stationId);
			return new StationClient(record.getUrl());

		} catch (UDDINamingException | StationClientException e) {
			throw new InvalidStationException();
		}
	}

	public void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException, NoBinaAvailException, NoCreditException, UserNotExistsException {
		User u = getUser(email);

		if (u.hasBina())
			throw new AlreadyHasBinaException();

		if (u.getCredit() <= 0)
			throw new NoCreditException();

		StationClient s = getStation(stationId);
		try {
			s.getBina();
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			throw new NoBinaAvailException();
		}

	}

	public void returnBina(String stationId, String email) throws FullStationException, InvalidStationException, NoBinaRentedException, UserNotExistsException {
		User u = getUser(email);

		if (!u.hasBina())
			throw new NoBinaRentedException();

		StationClient s = getStation(stationId);
		try {
			u.increaseCredit(s.returnBina());

		} catch (NoSlotAvail_Exception e) {
			throw new FullStationException();
		}
	}

	public User getUser(String email) throws UserNotExistsException {
		if (!users.containsKey(email))
			throw new UserNotExistsException();

		return users.get(email);
	}

	public User createUser(String email) throws EmailExistsException, InvalidEmailException {
		if (users.containsKey(email))
			throw new EmailExistsException();

		User u = new User(email);

		users.put(email, u);

		return u;
	}

	public void clear() {
		users.clear();
	}

	public void setEndpointManager(BinasEndpointManager endpointManager) {
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
