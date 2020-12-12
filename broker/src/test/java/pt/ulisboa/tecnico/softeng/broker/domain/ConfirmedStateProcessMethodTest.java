package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class ConfirmedStateProcessMethodTest extends RollbackTestAbstractClass {
	@Mocked
	private RestActivityBookingData activityReservationData;
	@Mocked
	private RestRentingData rentingData;
	@Mocked
	private RestRoomBookingData roomBookingData;
	@Mocked
	private BankInterface bankInterface;
	@Mocked
	private ActivityInterface activityInterface;
	@Mocked
	private HotelInterface roomInterface;
	@Mocked
	private CarInterface carInterface;
	@Mocked
	private TaxInterface taxInterface;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.begin, this.end, this.client, MARGIN);

		this.adventure.setState(State.CONFIRMED);
	}

	@Test
	public void successAll() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				CarInterface.getRentingData(RENTING_CONFIRMATION);

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.rentingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.rentingData.getInvoiceReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.roomBookingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.roomBookingData.getInvoiceReference();
				this.result = REFERENCE;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void successActivityAndHotel() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.roomBookingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.roomBookingData.getInvoiceReference();
				this.result = REFERENCE;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void successActivityAndCar() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				CarInterface.getRentingData(RENTING_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.rentingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.rentingData.getInvoiceReference();
				this.result = REFERENCE;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void successActivity() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void oneBankException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new BankException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void maxBankException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new BankException();
			}
		};

		for (int i = 0; i < ConfirmedState.MAX_BANK_EXCEPTIONS; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void maxMinusOneBankException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new BankException();
			}
		};

		for (int i = 0; i < ConfirmedState.MAX_BANK_EXCEPTIONS - 1; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionInPayment() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void activityException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
				this.result = new ActivityException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionInActivity() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void activityNoPaymentConfirmation() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void activityNoInvoiceReference() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void carException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				CarInterface.getRentingData(RENTING_CONFIRMATION);
				this.result = new CarException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteExceptionInCar() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				CarInterface.getRentingData(RENTING_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void carNoPaymentConfirmation() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				CarInterface.getRentingData(RENTING_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.rentingData.getPaymentReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void carNoInvoiceReference() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				CarInterface.getRentingData(RENTING_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.rentingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.rentingData.getInvoiceReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void hotelException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
				this.result = new HotelException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionInHotel() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.CONFIRMED, this.adventure.getState().getValue());
	}

	@Test
	public void hotelNoPaymentConfirmation() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.roomBookingData.getPaymentReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void hotelNoInvoiceReference() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		new Expectations() {
			{
				BankInterface.getOperationData(PAYMENT_CONFIRMATION);

				ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.activityReservationData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.activityReservationData.getInvoiceReference();
				this.result = REFERENCE;

				HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);

				ConfirmedStateProcessMethodTest.this.roomBookingData.getPaymentReference();
				this.result = REFERENCE;

				ConfirmedStateProcessMethodTest.this.roomBookingData.getInvoiceReference();
				this.result = null;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

}
