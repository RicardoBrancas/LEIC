package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class CarPersistenceTest {
	private static final String NAME1 = "eartz";
	private static final String PLATE_CAR1 = "aa-00-11";
	private static final String PLATE_CAR2 = "aa-00-12";
	private static final String DRIVING_LICENSE = "br123";
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final String NIF = "NIF";
	private static final String IBAN = "IBAN";
	private static final String IBAN_BUYER = "IBAN";

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		RentACar rentACar = new RentACar(NAME1, NIF, IBAN);
		Car car = new Car(PLATE_CAR1, 10, 10, rentACar);
		Motorcycle motorcycle = new Motorcycle(PLATE_CAR2, 20, 5, rentACar);
		car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER);
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		assertEquals(1, FenixFramework.getDomainRoot().getRentACarSet().size());

		RentACar rentACar = new ArrayList<>(FenixFramework.getDomainRoot().getRentACarSet()).get(0);
		assertEquals(2, rentACar.getVehicleSet().size());
		Processor processor = rentACar.getProcessor();
		assertEquals(NAME1, rentACar.getName());
		assertEquals(NIF, rentACar.getNif());
		assertEquals(IBAN, rentACar.getIban());
		assertNotNull(processor);
		assertEquals(1, processor.getRentingSet().size());

		for (Vehicle vehicle : rentACar.getVehicleSet()) {
			if (vehicle instanceof Car) {
				assertEquals(PLATE_CAR1.toUpperCase(), vehicle.getPlate());
				assertEquals(10, vehicle.getKilometers().intValue());
				assertEquals(10, vehicle.getPrice(), 0);
			}
			if (vehicle instanceof Motorcycle) {
				assertEquals(PLATE_CAR2.toUpperCase(), vehicle.getPlate());
				assertEquals(20, vehicle.getKilometers().intValue());
				assertEquals(5, vehicle.getPrice(), 0);
			}
		}

		for (Vehicle vehicle : rentACar.getVehicleSet()) {
			if (vehicle instanceof Car) {
				assertEquals(1, vehicle.getRentingSet().size());
				Renting renting = new ArrayList<>(vehicle.getRentingSet()).get(0);
				assertEquals(DRIVING_LICENSE, renting.getDrivingLicense());
				assertEquals(date1, renting.getBegin());
				assertEquals(date2, renting.getEnd());
				assertEquals(NIF, renting.getClientNif());
				assertEquals(IBAN, renting.getClientIban());
				assertNotNull(renting.getTime());
				assertNotNull(renting.getProcessor());
			}
			if (vehicle instanceof Motorcycle) {
				assertEquals(0, vehicle.getRentingSet().size());
			}
		}
	}

	@After
	@Atomic(mode = Atomic.TxMode.WRITE)
	public void tearDown() {
		for (RentACar rentACar : FenixFramework.getDomainRoot().getRentACarSet()) {
			rentACar.delete();
		}
	}

}
