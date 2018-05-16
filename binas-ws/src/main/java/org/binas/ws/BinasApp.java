package org.binas.ws;

import binas.ws.handler.BinasAuthorizationHandler;
import binas.ws.handler.KerberosServerHandler;
import org.binas.domain.BinasManager;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

import java.security.Key;

public class BinasApp {

	public static void main(String[] args) throws Exception {

		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BinasApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
			return;
		}
		String wsName = args[0];
		String wsURL = args[1];
		String uddiURL = args[2];
		int numberOfStations = Integer.parseInt(args[3]);

		System.out.println(BinasApp.class.getSimpleName() + " running");

		final String user = "binas@A60.binas.org";
		final String password = "mRuFZfNu";
		final Key key = SecurityHelper.generateKeyFromPassword(password);

		KerberosServerHandler.setKey(key);
		BinasAuthorizationHandler.setKey(key);

		BinasEndpointManager endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);

		BinasManager.getInstance().setNumberOfStations(numberOfStations);

		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}
}