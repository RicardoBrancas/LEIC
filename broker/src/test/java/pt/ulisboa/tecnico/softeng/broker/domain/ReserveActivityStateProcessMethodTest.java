package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class ReserveActivityStateProcessMethodTest extends RollbackTestAbstractClass {
	@Mocked
	private TaxInterface taxInterface;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.begin, this.end, this.client, MARGIN);

		this.adventure.setState(State.RESERVE_ACTIVITY);
	}

	@Test
	public void successNoBookRoom(@Mocked final ActivityInterface activityInterface) {
		Adventure sameDayAdventure = new Adventure(this.broker, this.begin, this.begin, this.client, MARGIN);
		sameDayAdventure.setState(State.RESERVE_ACTIVITY);

		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ACTIVITY_CONFIRMATION;
			}
		};

		sameDayAdventure.process();

		Assert.assertEquals(State.PROCESS_PAYMENT, sameDayAdventure.getState().getValue());
	}

	@Test
	public void successToRentVehicle(@Mocked final ActivityInterface activityInterface) {
		Adventure adv = new Adventure(this.broker, this.begin, this.begin, this.client, MARGIN, true);
		adv.setState(State.RESERVE_ACTIVITY);

		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ACTIVITY_CONFIRMATION;
			}
		};

		adv.process();

		Assert.assertEquals(State.RENT_VEHICLE, adv.getState().getValue());
	}

	@Test
	public void successBookRoom(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ACTIVITY_CONFIRMATION;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void activityException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new ActivityException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void singleRemoteAccessException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.RESERVE_ACTIVITY, this.adventure.getState().getValue());
	}

	@Test
	public void maxRemoteAccessException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void maxMinusOneRemoteAccessException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.RESERVE_ACTIVITY, this.adventure.getState().getValue());
	}

	@Test
	public void twoRemoteAccessExceptionOneSuccess(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 2) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							return ACTIVITY_CONFIRMATION;
						}
					}
				};
				this.times = 3;

			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionOneActivityException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							throw new ActivityException();
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