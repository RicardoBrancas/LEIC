package org.binas.ws;

import org.binas.domain.BinasManager;

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

		BinasEndpointManager endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);

		System.out.println(BinasApp.class.getSimpleName() + " running");

		BinasManager.getInstance().setNumberOfStations(numberOfStations);

		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}
}