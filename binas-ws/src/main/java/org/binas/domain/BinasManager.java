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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BinasManager {
	private static final Logger logger = Logger.getLogger(BinasManager.class.getName());

	private final AtomicInteger initialCredit;
	private final Map<String, User> userCache = new ConcurrentHashMap<>();
	private int numberOfStations = 3;
	private BinasEndpointManager endpointManager;

	private BinasManager() {
		initialCredit = new AtomicInteger(10);
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

	public void setInitialCredit(int initialCredit) {
		this.initialCredit.set(initialCredit);
	}

	public void setNumberOfStations(int numberOfStations) {
		this.numberOfStations = numberOfStations;
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
		return new ArrayList<>(); //should never happen
	}

	/**
	 * @return number of replies required to obtain quorum consensus
	 */
	public int getQC() {
		return numberOfStations / 2 + 1;
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

		while (true) {
			try {
				s.getBina();
				u.setHasBina(true);
				u.decreaseCredit(1);
				return;

			} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
				throw new NoBinaAvailException("No Binas available at station " + s.getInfo().getId());

			} catch (QuorumNotReachedException e) {
				System.out.println("Quorum not reached. Trying again...");

			}
		}
	}

	public synchronized void returnBina(String stationId, String email) throws FullStationException, InvalidStationException, NoBinaRentedException, UserNotExistsException {
		User u = getUser(email);

		if (!u.hasBina())
			throw new NoBinaRentedException("User " + u.getEmail() + " has no Bina to return.");

		StationClient s = getStation(stationId);

		while (true) {
			try {
				int credit = s.returnBina();
				u.increaseCredit(credit);
				u.setHasBina(false);
				return;

			} catch (NoSlotAvail_Exception e) {
				throw new FullStationException("Station " + s.getInfo().getId() + " has no free docks.");

			} catch (QuorumNotReachedException e) {
				System.out.println("Quorum not reached. Trying again...");

			}
		}
	}

	/**
	 * Attempts find a user by its email.
	 * First, checks BinasManager's local cache. If the user is there, it's returned.
	 * If the user isn't there, all the stations are queried about it.
	 * If a quorum is reached, the user is returned,
	 * Otherwise, it is assumed the user doesn't exist, and an exception is thrown.
	 *
	 * @param email
	 * @return the user
	 * @throws UserNotExistsException
	 */
	public synchronized User getUser(String email) throws UserNotExistsException {
		if (email == null) {
			throw new UserNotExistsException("Email is null");
		} else if (userCache.containsKey(email)) {
			logger.info("Using user " + email + " from cache");

			return userCache.get(email);

		} else {
			QuorumConsensus<User.Replica> qc = new QuorumConsensusGetBalance(listStations(), getQC(), email);
			qc.run();


			try {
				User.Replica replica = qc.get();

				logger.info(String.format("Found user in replicas. Adding to cache (%s, %d, %d)", replica.getEmail(), replica.getBalance(), replica.getTag()));
				User user = new User(replica.getEmail(), replica.getBalance());

				user.setTag(replica.getTag());
				userCache.put(user.getEmail(), user);

				return user;

			} catch (QuorumNotReachedException e) {
				throw new UserNotExistsException("User not found in stations. Assuming it doesn't exist.");

			} catch (InvalidEmailException e) {
				throw new UserNotExistsException();
			}
		}
	}

	/**
	 * Checks if the user already exists. If not, a new user is created.
	 *
	 * @param email
	 * @return
	 * @throws EmailExistsException  if the email has already been registered
	 * @throws InvalidEmailException if the email does not have a valid format
	 */
	public synchronized User createUser(String email) throws EmailExistsException, InvalidEmailException {

		try {
			getUser(email);
			throw new EmailExistsException();

		} catch (UserNotExistsException e) {
			User u = new User(email, initialCredit.get());

			while (true) {
				try {
					u.setCredit(initialCredit.get());
					userCache.put(email, u);
					return u;

				} catch (QuorumNotReachedException ex) {
					System.out.println("Quorum not reached. Trying again...");
				}
			}
		}
	}

	public void clear() {
		userCache.clear();
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
