package org.binas.domain;

import org.binas.domain.exception.*;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class BinasManager {

	private final Map<String, User> users = new HashMap<>();
	private final Map<String, StationClient> stations = new HashMap<>();

	private BinasManager() {
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public List<StationClient> getStations() {
		return new ArrayList<>(stations.values());
	}

	public StationClient getStation(String stationId) throws InvalidStationException {
		StationClient s = stations.get(stationId);
		if (s == null) {
			throw new InvalidStationException();
		}
		return s;
	}

	public void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException, NoBinaAvailException, NoCreditException, UserNotExistsException {
		User u = getUser(email);
		if (u.hasBina()) {
			throw new AlreadyHasBinaException();
		}
		if (u.getCredit() <= 0) {
			throw new NoCreditException();
		}

		StationClient s = getStation(stationId);
		try {
			s.getBina();
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			throw new NoBinaAvailException();
		}

	}

	public void returnBina(String stationId, String email) throws FullStationException, InvalidStationException, NoBinaRentedException, UserNotExistsException {
		User u = getUser(email);
		int result = 0;
		if (!u.hasBina()) {
			throw new NoBinaRentedException();
		}

		StationClient s = getStation(stationId);
		try {
			result = s.returnBina();
		} catch (NoSlotAvail_Exception e) {
			throw new FullStationException();
		}
		u.increaseCredit(result);
	}

	public User getUser(String email) throws UserNotExistsException {
		User u = null;
		users.get(email);
		if (u == null) {
			throw new UserNotExistsException();
		}
		return u;
	}

	;

	public User createUser(String email) throws EmailExistsException, InvalidEmailException {
		if (users.containsKey(email))
			throw new EmailExistsException();

		User u = new User(email);

		users.put(email, u);

		return u;
	}

	public void clear() {
		stations.clear();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}


	// TODO
	//im not sure what is left. Constructor?

}
