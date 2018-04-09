package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.domain.exception.*;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.BinasPortType;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.binas.ws.UserView;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.FullStation_Exception;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebService(
		name = "BinasWebService",
		targetNamespace = "http://ws.binas.org/",
		serviceName = "BinasService",
		portName = "BinasPort",
		wsdlLocation = "binas.1_0.wsdl",
		endpointInterface = "org.binas.ws.BinasPortType"
)
public class BinasPortImpl implements BinasPortType {
    /*
    * TODO: Many things done here maybe should be done in BinasManager
    * /
     */
	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private BinasEndpointManager endpointManager;

	/**
	 * Constructor receives a reference to the endpoint manager.
	 */

	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	private CoordinatesView convertCoordinatesView(org.binas.station.ws.CoordinatesView cv){
		CoordinatesView retCv = new CoordinatesView();
		retCv.setX(cv.getX());
		retCv.setY(cv.getY());
		return retCv;
	}

	private StationView convertStationView(org.binas.station.ws.StationView sv){

		StationView retSv = new StationView();
		CoordinatesView lol = retSv.getCoordinate();
		retSv.setId(sv.getId());
		retSv.setCoordinate(convertCoordinatesView(sv.getCoordinate()));
		retSv.setCapacity(sv.getCapacity());
		retSv.setTotalGets(sv.getTotalGets());
		retSv.setTotalReturns(sv.getTotalReturns());
		retSv.setAvailableBinas(sv.getAvailableBinas());
		retSv.setFreeDocks(sv.getFreeDocks());
		return retSv;
	}
	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates){
		/*TODO
		 * Make this readable
		 * Verify if sort should be done HERE
		 * Verify if sort metric should be Manhatan distance
		 * */
		List<StationView> ret = (BinasManager.getInstance().getStations().stream().
				map(u -> convertStationView(u.getInfo())).
				collect(Collectors.toList()));
		ret.sort((StationView v1, StationView v2) ->
                        Math.abs(v1.getCoordinate().getX()+v1.getCoordinate().getY()
                                - coordinates.getX() - coordinates.getY())-
                                Math.abs(v2.getCoordinate().getX()+v2.getCoordinate().getY()
                                        - coordinates.getX() - coordinates.getY())
        );
		return ret;

	}
	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		org.binas.station.ws.StationView sv = null;
		try {
			 sv = BinasManager.getInstance().getStation(stationId).getInfo();
		} catch (InvalidStationException e) {
			e.throwWSException();
		}
		return convertStationView(sv);
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		int credit = 0;
		try{
			credit = BinasManager.getInstance().getUser(email).getCredit();
		} catch (UserNotExistsException e){
			e.throwWSException();
		}
		return credit;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		User u = null;

		try {
			u = BinasManager.getInstance().createUser(email);
		} catch (EmailExistsException e) {
			e.throwWSException();
		} catch (InvalidEmailException e) {
			e.throwWSException();
		}

		return u.getView();
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		try {
			BinasManager.getInstance().rentBina(stationId, email);
		} catch (UserNotExistsException e){
			e.throwWSException();
		} catch (AlreadyHasBinaException e){
			e.throwWSException();
		} catch (InvalidStationException e) {
			e.throwWSException();
		} catch (NoCreditException e) {
			e.throwWSException();
		} catch (NoBinaAvailException e){
		    e.throwWSException();
        }
	}


	@Override
	public void returnBina(String stationId, String email)throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
			try {
				BinasManager.getInstance().returnBina(stationId, email);
			} catch (UserNotExistsException e) {
				e.throwWSException();
			} catch (FullStationException e) {
				e.throwWSException();
			} catch (InvalidStationException e) {
				e.throwWSException();
			} catch (NoBinaRentedException e) {
				e.throwWSException();
			}
	}

	@Override
	public String testPing(String inputMessage) {
		return "Received: " + inputMessage;
	}

	@Override
	public void testClear() {
        //TODO
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
        //TODO
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
        //TODO
	}
}
