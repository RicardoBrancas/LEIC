package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;

public class Hotel extends Hotel_Base {
	static final int CODE_SIZE = 7;

	public Hotel(String code, String name, String nif, String iban, double priceSingle, double priceDouble) {
		checkArguments(code, name, nif, iban, priceSingle, priceDouble);

		setCode(code);
		setName(name);
		setNif(nif);
		setIban(iban);
		setPriceSingle(priceSingle);
		setPriceDouble(priceDouble);

		setProcessor(new Processor());

		FenixFramework.getDomainRoot().addHotel(this);
	}

	public void delete() {
		setRoot(null);

		getProcessor().delete();

		for (Room room : getRoomSet()) {
			room.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(String code, String name, String nif, String iban, double priceSingle,
			double priceDouble) {
		if (code == null || name == null || isEmpty(code) || isEmpty(name) || nif == null || isEmpty(nif)
				|| iban == null || isEmpty(iban) || priceSingle < 0 || priceDouble < 0) {

			throw new HotelException();
		}

		if (code.length() != Hotel.CODE_SIZE) {
			throw new HotelException();
		}

		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			if (hotel.getCode().equals(code)) {
				throw new HotelException();
			}
		}

		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			if (hotel.getNif().equals(nif)) {
				throw new HotelException();
			}
		}
	}

	public Room hasVacancy(Room.Type type, LocalDate arrival, LocalDate departure) {
		if (type == null || arrival == null || departure == null) {
			throw new HotelException();
		}

		for (Room room : getRoomSet()) {
			if (room.isFree(type, arrival, departure)) {
				return room;
			}
		}
		return null;
	}

	public Set<Room> getAvailableRooms(LocalDate arrival, LocalDate departure) {
		Set<Room> availableRooms = new HashSet<>();
		for (Room room : getRoomSet()) {
			if (room.isFree(room.getType(), arrival, departure)) {
				availableRooms.add(room);
			}
		}
		return availableRooms;
	}

	public double getPrice(Room.Type type) {
		if (type == null) {
			throw new HotelException();
		} else {
			return type.equals(Room.Type.SINGLE) ? getPriceSingle() : getPriceDouble();
		}
	}

	public void setPrice(Room.Type type, double price) {
		if (price < 0 || type == null) {
			throw new HotelException();
		} else if (type.equals(Room.Type.SINGLE)) {
			setPriceSingle(price);
		} else {
			setPriceDouble(price);
		}
	}

	private boolean isEmpty(String str) {
		return str.trim().length() == 0;
	}

	@Override
	public void addRoom(Room room) {
		if (hasRoom(room.getNumber())) {
			throw new HotelException();
		}

		super.addRoom(room);
	}

	public boolean hasRoom(String number) {
		for (Room room : getRoomSet()) {
			if (room.getNumber().equals(number)) {
				return true;
			}
		}
		return false;
	}

	public Booking getBooking(String reference) {
		for (Room room : getRoomSet()) {
			Booking booking = room.getBooking(reference);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	public static String reserveRoom(Room.Type type, LocalDate arrival, LocalDate departure, String buyerNIF,
			String buyerIban) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			Room room = hotel.hasVacancy(type, arrival, departure);
			if (room != null) {
				Booking booking = room.reserve(type, arrival, departure, buyerNIF, buyerIban);
				return booking.getReference();
			}
		}
		throw new HotelException();
	}

	public static String cancelBooking(String reference) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			Booking booking = hotel.getBooking(reference);
			if (booking != null) {
				return booking.cancel();
			}
		}
		throw new HotelException();
	}

	public static RoomBookingData getRoomBookingData(String reference) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			for (Room room : hotel.getRoomSet()) {
				Booking booking = room.getBooking(reference);
				if (booking != null) {
					return new RoomBookingData(booking);
				}
			}
		}
		throw new HotelException();
	}

	public static List<Room> getAvailableRooms(int number, LocalDate arrival, LocalDate departure) {
		List<Room> availableRooms = new ArrayList<>();
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			availableRooms.addAll(hotel.getAvailableRooms(arrival, departure));
			if (availableRooms.size() >= number) {
				return availableRooms;
			}
		}
		return availableRooms;
	}

	public static Hotel getHotelByCode(String code) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			if (hotel.getCode().equals(code)) {
				return hotel;
			}
		}
		return null;
	}

	public Room getRoomByNumber(String number) {
		return getRoomSet().stream().filter(r -> r.getNumber().equals(number)).findFirst().orElse(null);

	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	public Booking getBooking4AdventureId(String adventureId) {
		return getRoomSet().stream().flatMap(r -> r.getBookingSet().stream())
				.filter(b -> b.getAdventureId() != null && b.getAdventureId().equals(adventureId)).findFirst()
				.orElse(null);
	}

	public Collection<? extends Booking> getBookings4BulkId(String bulkId) {
		return getRoomSet().stream().flatMap(r -> r.getBookingSet().stream())
				.filter(b -> b.getBulkId() != null && b.getBulkId().equals(bulkId)).collect(Collectors.toSet());
	}

}
