package org.binas.ws;

import javax.jws.WebService;
import java.util.List;

@WebService(
		name = "BinasWebService",
		targetNamespace = "http://ws.binas.org/",
		serviceName = "BinasService",
		portName = "BinasPort",
		wsdlLocation = "binas.1_0.wsdl",
		endpointInterface = "org.binas.ws.BinasPortType"
)
public class BinasPortImpl implements BinasPortType {
	
	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		return null;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		return null;
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		return 0;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		return null;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {

	}

	@Override
	public void returnBina(String stationId, String email) throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {

	}

	@Override
	public String testPing(String inputMessage) {
		return null;
	}

	@Override
	public void testClear() {

	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {

	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {

	}
}
