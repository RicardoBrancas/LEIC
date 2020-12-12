package pt.ulisboa.tecnico.softeng.broker.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class RentVehicleState extends RentVehicleState_Base {
	private static Logger logger = LoggerFactory.getLogger(RentVehicleState.class);

	public static final int MAX_REMOTE_ERRORS = 5;

	@Override
	public State getValue() {
		return State.RENT_VEHICLE;
	}

	@Override
	public void process() {
		try {
			// For now we will only reserve cars
			String reference = CarInterface.rentCar(Type.CAR, getAdventure().getClient().getDrivingLicense(),
					getAdventure().getBroker().getNifAsBuyer(), getAdventure().getBroker().getIban(),
					getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getID());

			getAdventure().setRentingConfirmation(reference);

			RestRentingData restRentingData = CarInterface.getRentingData(reference);

			getAdventure().incAmountToPay(restRentingData.getPrice());
		} catch (CarException ce) {
			getAdventure().setState(State.UNDO);
			return;
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				getAdventure().setState(State.UNDO);
			}
			return;
		}

		getAdventure().setState(State.PROCESS_PAYMENT);
	}

}
