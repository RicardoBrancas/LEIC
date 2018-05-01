package org.binas.station.domain;

import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;
import org.binas.station.domain.exception.UserNotExistsException;

import javax.xml.ws.Holder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Domain Root.
 */
public class Station {

	/**
	 * Creates and returns default coordinates.
	 */
	private static final Coordinates DEFAULT_COORDINATES = new Coordinates(5, 5);
	private static final int DEFAULT_MAX_CAPACITY = 20;
	private static final int DEFAULT_BONUS = 0;

	private static Map<String, User> users = new HashMap<>();

	/**
	 * Station identifier.
	 */
	private String id;

	/**
	 * Station location coordinates.
	 */
	private Coordinates coordinates;

	/**
	 * Maximum capacity of station.
	 */
	private int maxCapacity;

	/**
	 * Bonus for returning bike at this station.
	 */
	private int bonus;

	/**
	 * Global counter of Binas Gets. Uses lock-free thread-safe single variable.
	 * This means that multiple threads can update this variable concurrently with
	 * correct synchronization.
	 */
	private AtomicInteger totalGets = new AtomicInteger(0);

	/**
	 * Global counter of Binas Returns. Uses lock-free thread-safe single variable.
	 */
	private AtomicInteger totalReturns = new AtomicInteger(0);

	/**
	 * Global with current number of free docks. Uses lock-free thread-safe single variable.
	 */
	private AtomicInteger freeDocks = new AtomicInteger(0);

	// Singleton -------------------------------------------------------------

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private Station() {
		//Initialization of default values
		reset();
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		private static final Station INSTANCE = new Station();
	}

	/**
	 * Synchronized locks object to configure initial values
	 */
	public synchronized void init(int x, int y, int capacity, int returnPrize) throws BadInitException {

		if (x < 0) {
			throw new BadInitException("X coordinate must be positive.");
		} else if (y < 0) {
			throw new BadInitException("Y coordinate must be positive.");
		} else if (capacity < 0) {
			throw new BadInitException("Initial station capacity must be positive.");
		} else if (returnPrize < 0) {
			throw new BadInitException("Return prize must be positive.");
		}

		this.coordinates = new Coordinates(x, y);
		this.maxCapacity = capacity;
		this.bonus = returnPrize;

		freeDocks.set(0);
		totalGets.set(0);
		totalReturns.set(0);
	}

	public synchronized void reset() {
		maxCapacity = DEFAULT_MAX_CAPACITY;
		bonus = DEFAULT_BONUS;
		coordinates = DEFAULT_COORDINATES;

		freeDocks.set(0);
		totalGets.set(0);
		totalReturns.set(0);

		users.clear();
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Synchronized locks object before attempting to return Bina
	 */
	public synchronized int returnBina() throws NoSlotAvailException {
		if (getFreeDocks() == 0)
			throw new NoSlotAvailException("Station " + getId() + " has no free slots.");
		freeDocks.decrementAndGet();
		totalReturns.incrementAndGet();
		return getBonus();
	}

	/**
	 * Synchronized locks object before attempting to get Bina
	 */
	public synchronized void getBina() throws NoBinaAvailException {
		if (getFreeDocks() == getMaxCapacity())
			throw new NoBinaAvailException("There are no Binas at this station.");
		freeDocks.incrementAndGet();
		totalGets.incrementAndGet();
	}


	public synchronized void getBalance(String email, Holder<Integer> balance, Holder<Integer> tag) throws UserNotExistsException {
		User user = users.get(email);
		if (user == null) {
			throw new UserNotExistsException();
		}
		balance.value = user.getBalance();
		tag.value = user.getTag();
	}

	public synchronized void setBalance(String email, Integer balance, Integer tag) {
		User user = users.get(email);

		if (user == null) {
			users.put(email, new User(email, balance, tag));
		} else if (user.getTag() < tag) {
			user.setTag(tag);
			user.setBalance(balance);
		}
	}

	// Getters -------------------------------------------------------------

	public static synchronized Station getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public String getId() {
		return id;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Synchronized locks object before returning max capacity
	 */
	public synchronized int getMaxCapacity() {
		return maxCapacity;
	}

	public int getTotalGets() {
		return totalGets.get();
	}

	public int getTotalReturns() {
		return totalReturns.get();
	}

	public int getFreeDocks() {
		return freeDocks.get();
	}

	/**
	 * Synchronized locks object before returning bonus
	 */
	public synchronized int getBonus() {
		return bonus;
	}

	/**
	 * Synchronized locks object before returning available Binas
	 */
	public synchronized int getAvailableBinas() {
		return maxCapacity - freeDocks.get();
	}

}
