package pt.ulisboa.tecnico.softeng.car.services.local;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.car.domain.Car;
import pt.ulisboa.tecnico.softeng.car.domain.Motorcycle;
import pt.ulisboa.tecnico.softeng.car.domain.RentACar;
import pt.ulisboa.tecnico.softeng.car.domain.Renting;
import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentACarData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.VehicleData;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestRentingData;

public class RentACarInterface {
	private static Logger logger = LoggerFactory.getLogger(RentACarInterface.class);

	@Atomic(mode = Atomic.TxMode.READ)
	public static List<RentACarData> getRentACars() {
		return FenixFramework.getDomainRoot().getRentACarSet().stream()
				.map(r -> new RentACarData(r.getCode(), r.getName(), r.getNif(), r.getIban(), r.getVehicleSet().size()))
				.collect(Collectors.toList());
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static void createRentACar(RentACarData rentACarData) {
		new RentACar(rentACarData.getName(), rentACarData.getNif(), rentACarData.getIban());
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static List<VehicleData> getVehicles(String code) {
		RentACar rentACar = getRentACar(code);
		return rentACar.getVehicleSet().stream().map(v -> new VehicleData(getVehicleType(v), v.getPlate(),
				v.getKilometers(), v.getPrice(), toRentACarData(v.getRentACar()))).collect(Collectors.toList());
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static List<RentingData> getRentings(String code, String plate) {
		Vehicle vehicle = getVehicle(code, plate);
		return vehicle.getRentingSet().stream().map(RentingData::new).collect(Collectors.toList());
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static RentingData getRentingData(String code, String plate, String reference) {
		Renting renting = getVehicle(code, plate).getRentingSet().stream()
				.filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

		return renting == null ? new RentingData() : new RentingData(renting);
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static RestRentingData getRentingData(String reference) {
		Renting renting = FenixFramework.getDomainRoot().getRentACarSet().stream()
				.flatMap(rac -> rac.getVehicleSet().stream()).flatMap(v -> v.getRentingSet().stream())
				.filter(r -> r.getReference().equals(reference)).findFirst().orElseThrow(() -> new CarException());

		return new RestRentingData(renting);
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static RentingData cancelRenting(String code, String plate, String reference) {
		Renting renting = getVehicle(code, plate).getRentingSet().stream()
				.filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

		if (renting == null) {
			return new RentingData();
		} else {
			renting.cancel();
			return new RentingData(renting);
		}
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static String cancelRenting(String reference) {
		Renting renting = getRenting(reference);
		if (renting != null) {
			return renting.getCancellationReference();
		}

		renting = FenixFramework.getDomainRoot().getRentACarSet().stream().flatMap(rac -> rac.getVehicleSet().stream())
				.flatMap(v -> v.getRentingSet().stream()).filter(r -> r.getReference().equals(reference)).findFirst()
				.orElseThrow(() -> new CarException());

		renting.cancel();
		return renting.getCancellationReference();
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static RentingData checkoutRenting(String code, String plate, String reference, Integer kms) {
		if (kms == null) {
			throw new CarException();
		}

		Renting renting = getVehicle(code, plate).getRentingSet().stream()
				.filter(r -> r.getReference().equals(reference)).findFirst().orElse(null);

		if (renting == null) {
			return new RentingData();
		} else {
			renting.checkout(kms);
			return new RentingData(renting);
		}
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static RentACarData getRentACarData(String code) {
		return toRentACarData(getRentACar(code));
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static void createVehicle(String code, VehicleData vehicleData) {
		if (vehicleData.getKilometers() == null || vehicleData.getPrice() == null) {
			throw new CarException();
		}

		RentACar rentACar = getRentACar(code);
		if (vehicleData.getType() == Vehicle.Type.CAR) {
			new Car(vehicleData.getPlate(), vehicleData.getKilometers(), vehicleData.getPrice(), rentACar);
		} else {
			new Motorcycle(vehicleData.getPlate(), vehicleData.getKilometers(), vehicleData.getPrice(), rentACar);
		}
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static VehicleData getVehicleByPlate(String code, String plate) {
		return getVehicles(code).stream().filter(v -> v.getPlate().equals(plate)).findFirst().orElse(null);
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static String rent(String code, String plate, String drivingLicense, String buyerNIF, String buyerIBAN,
			LocalDate begin, LocalDate end, String adventureId) {

		Renting renting = getReting4AdventureId(adventureId);
		if (renting != null) {
			return renting.getReference();
		}

		return getVehicle(code, plate).rent(drivingLicense, begin, end, buyerNIF, buyerIBAN).getReference();
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static String rent(String type, String license, String nif, String iban, LocalDate begin, LocalDate end,
			String adventureId) {
		Renting renting = getReting4AdventureId(adventureId);
		if (renting != null) {
			return renting.getReference();
		}

		return RentACar.rent(type.equals("CAR") ? Car.class : Motorcycle.class, license, nif, iban, begin, end);
	}

	@Atomic(mode = Atomic.TxMode.READ)
	public static VehicleData getVehicleData(String code, String plate) {
		Vehicle v = getVehicle(code, plate);
		return new VehicleData(getVehicleType(v), v.getPlate(), v.getKilometers(), v.getPrice(), getRentACarData(code));
	}

	private static Vehicle getVehicle(String code, String plate) {
		return getRentACar(code).getVehicleSet().stream().filter(v -> v.getPlate().equals(plate)).findFirst()
				.orElse(null);
	}

	private static RentACarData toRentACarData(RentACar rentACar) {
		return new RentACarData(rentACar.getCode(), rentACar.getName(), rentACar.getNif(), rentACar.getIban(),
				rentACar.getVehicleSet().size());
	}

	private static RentACar getRentACar(String code) {
		return FenixFramework.getDomainRoot().getRentACarSet().stream().filter(h -> h.getCode().equals(code))
				.findFirst().orElse(null);
	}

	private static Vehicle.Type getVehicleType(Vehicle vehicle) {
		if (vehicle instanceof Car) {
			return Vehicle.Type.CAR;
		} else {
			return Vehicle.Type.MOTORCYCLE;
		}
	}

	@Atomic(mode = Atomic.TxMode.WRITE)
	public static void deleteRentACars() {
		for (RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
			rentACar.delete();
		}
	}

	public static Renting getRenting(String reference) {
		Renting renting = FenixFramework.getDomainRoot().getRentACarSet().stream()
				.flatMap(rac -> rac.getVehicleSet().stream()).flatMap(v -> v.getRentingSet().stream())
				.filter(r -> r.getReference().equals(reference)).findFirst().orElseThrow(() -> new CarException());

		return renting;
	}

	private static Renting getReting4AdventureId(String adventureId) {
		for (RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
			Renting renting = rentACar.getRenting4AdventureId(adventureId);
			if (renting != null) {
				return renting;
			}
		}
		return null;
	}

}
