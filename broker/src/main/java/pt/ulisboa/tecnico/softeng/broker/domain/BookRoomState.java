package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BookRoomState extends BookRoomState_Base {
	public static final int MAX_REMOTE_ERRORS = 10;

	@Override
	public State getValue() {
		return State.BOOK_ROOM;
	}

	@Override
	public void process() {
		try {
			getAdventure().setRoomConfirmation(HotelInterface.reserveRoom(new RestRoomBookingData(Type.SINGLE,
					getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNifAsBuyer(),
					getAdventure().getBroker().getIban(), getAdventure().getID())));
			getAdventure()
					.incAmountToPay(HotelInterface.getRoomBookingData(getAdventure().getRoomConfirmation()).getPrice());
		} catch (HotelException he) {
			getAdventure().setState(State.UNDO);
			return;
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				getAdventure().setState(State.UNDO);
			}
			return;
		}

		if (getAdventure().shouldRentVehicle()) {
			getAdventure().setState(State.RENT_VEHICLE);
		} else {
			getAdventure().setState(State.PROCESS_PAYMENT);
		}
	}

}
