package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class RentVehicleStateMethodTest extends RollbackTestAbstractClass {
	@Mocked
	private TaxInterface taxInterface;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.begin, this.end, this.client, MARGIN);

		this.adventure.setState(State.RENT_VEHICLE);
	}

	@Test
	public void successRentVehicle(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = RENTING_CONFIRMATION;
				this.times = 1;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
	}

	@Test
	public void carException(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new CarException();
				this.times = 1;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void singleRemoteAccessException(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new RemoteAccessException();
				this.times = 1;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.RENT_VEHICLE, this.adventure.getState().getValue());
	}

	@Test
	public void maxRemoteAccessException(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new RemoteAccessException();
				this.times = RentVehicleState.MAX_REMOTE_ERRORS;
			}
		};

		for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void maxMinusOneRemoteAccessException(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new RemoteAccessException();
				this.times = RentVehicleState.MAX_REMOTE_ERRORS - 1;
			}
		};

		for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.RENT_VEHICLE, this.adventure.getState().getValue());
	}

	@Test
	public void twoRemoteAccessExceptionOneSuccess(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 2) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							return RENTING_CONFIRMATION;
						}
					}
				};
				this.times = 3;
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionOneCarException(@Mocked final CarInterface carInterface) {
		new Expectations() {
			{
				CarInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
						RentVehicleStateMethodTest.this.begin, RentVehicleStateMethodTest.this.end, this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							throw new CarException();
						}
					}
				};
				this.times = 2;
			}
		};

		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

}