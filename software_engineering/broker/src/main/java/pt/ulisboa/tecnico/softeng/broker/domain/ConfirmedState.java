package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class ConfirmedState extends ConfirmedState_Base {
	public static int MAX_REMOTE_ERRORS = 20;
	public static int MAX_BANK_EXCEPTIONS = 5;

	public ConfirmedState() {
		super();
		setNumberOfBankExceptions(0);
	}

	@Override
	public State getValue() {
		return State.CONFIRMED;
	}

	@Override
	public void process() {
		try {
			BankInterface.getOperationData(getAdventure().getPaymentConfirmation());
		} catch (BankException be) {
			setNumberOfBankExceptions(getNumberOfBankExceptions() + 1);
			if (getNumberOfBankExceptions() == MAX_BANK_EXCEPTIONS) {
				getAdventure().setState(State.UNDO);
			}
			return;
		} catch (final RemoteAccessException rae) {
			return;
		}

		resetNumOfRemoteErrors();
		setNumberOfBankExceptions(0);

		RestActivityBookingData reservation;
		try {
			reservation = ActivityInterface.getActivityReservationData(getAdventure().getActivityConfirmation());
		} catch (ActivityException ae) {
			getAdventure().setState(State.UNDO);
			return;
		} catch (final RemoteAccessException rae) {
			return;
		}
		if (reservation.getPaymentReference() == null || reservation.getInvoiceReference() == null) {
			getAdventure().setState(State.UNDO);
			return;
		}

		if (getAdventure().getRentingConfirmation() != null) {
			RestRentingData rentingData;
			try {
				rentingData = CarInterface.getRentingData(getAdventure().getRentingConfirmation());
			} catch (CarException he) {
				getAdventure().setState(State.UNDO);
				return;
			} catch (RemoteAccessException rae) {
				return;
			}
			if (rentingData.getPaymentReference() == null || rentingData.getInvoiceReference() == null) {
				getAdventure().setState(State.UNDO);
				return;
			}
		}

		if (getAdventure().getRoomConfirmation() != null) {
			RestRoomBookingData booking;
			try {
				booking = HotelInterface.getRoomBookingData(getAdventure().getRoomConfirmation());
			} catch (final HotelException he) {
				getAdventure().setState(State.UNDO);
				return;
			} catch (final RemoteAccessException rae) {
				return;
			}
			if (booking.getPaymentReference() == null || booking.getInvoiceReference() == null) {
				getAdventure().setState(State.UNDO);
				return;
			}
		}
	}

}
