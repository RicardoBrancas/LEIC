package org.binas.ws.cli;

import org.binas.ws.*;
import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;

import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;

public class BinasClientApp {

	private static SecureRandom random = new SecureRandom();

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BinasClientApp.class.getName() + " wsURL kerbyURL OR uddiURL wsName kerbyURL");
			return;
		}

		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		String kerbyURL;
		if (args.length == 2) {
			wsURL = args[0];
			kerbyURL = args[1];
		} else {
			uddiURL = args[0];
			wsName = args[1];
			kerbyURL = args[2];
		}

		System.out.println(BinasClientApp.class.getSimpleName() + " running");

		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		// === AUTH ===

		CipheredView cTicket;
		SessionKey sessionKey;
		CipheredView cAuth;

		System.out.printf("Creating kerby client for kerby server at %s\n", kerbyURL);
		KerbyClient kerbyClient = new KerbyClient(kerbyURL);
		while (true) {
			try {
				System.out.print("User: ");
				System.out.flush();
				String user = input.readLine();

				System.out.print("Password: ");
				System.out.flush();
				String password = input.readLine();

				long nonce = random.nextLong();

				final Key clientKey = SecurityHelper.generateKeyFromPassword(password);

				SessionKeyAndTicketView sessionKeyAndTicket = kerbyClient.requestTicket(user, "binas@A60.binas.org", nonce, 60);

				CipheredView cSessionKey = sessionKeyAndTicket.getSessionKey();
				cTicket = sessionKeyAndTicket.getTicket();

				sessionKey = new SessionKey(cSessionKey, clientKey);

				Auth auth = new Auth(user, new Date());
				cAuth = auth.cipher(sessionKey.getKeyXY());

				break;
			} catch (KerbyException | BadTicketRequest_Exception ex) {
				System.out.println("Invalid credentials. Please try again...");
			}

		}

		// === BINAS CONNECTION ===

		BinasClient binasClient = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			binasClient = new BinasClient(wsURL);

		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n",
					uddiURL, wsName);
			binasClient = new BinasClient(uddiURL, wsName);
		}

		BindingProvider bindingProvider = (BindingProvider) binasClient.port;
		bindingProvider.getRequestContext().put("ticket", cTicket);
		bindingProvider.getRequestContext().put("key", sessionKey);
		bindingProvider.getRequestContext().put("auth", cAuth);

		System.out.println("Waiting for commands. Type 'help' if you need it...");
		while (true) {
			System.out.print("> ");
			System.out.flush();
			String command[] = input.readLine().split(" ");

			if (command.length < 1) {
				System.out.println("Please insert a command.");
			}

			try {
				switch (command[0]) {
					case "quit":
						System.exit(0);
						break;

					case "help":
						System.out.println("quit");
						System.out.println("help");
						System.out.println("mkuser <email>");
						System.out.println("read <email>");
						System.out.println("rent <station> <email>");
						System.out.println("return <station> <email>");
						break;

					case "mkuser":
						if (command.length < 2) {
							System.out.println("Syntax is mkuser <email>");
							continue;
						}

						UserView userView = binasClient.activateUser(command[1]);
						if (userView != null)
							System.out.printf("(%s, %d, %b)\n", userView.getEmail(), userView.getCredit(), userView.isHasBina());
						else
							System.out.println("null");

						break;

					case "read":
						if (command.length < 2) {
							System.out.println("Syntax is read <email>");
							continue;
						}

						System.out.printf("credit: %d\n", binasClient.getCredit(command[1]));
						break;

					case "rent":
						if (command.length < 3) {
							System.out.println("Syntax is rent <station> <email>");
							continue;
						}

						binasClient.rentBina(command[1], command[2]);
						break;

					case "return":
						if (command.length < 3) {
							System.out.println("Syntax is return <station> <email>");
							continue;
						}

						binasClient.returnBina(command[1], command[2]);
						break;

					default:
						System.out.println("Invalid command. Type 'help' if you need it");

				}
			} catch (UserNotExists_Exception | EmailExists_Exception | NoCredit_Exception | NoBinaRented_Exception | InvalidStation_Exception | NoBinaAvail_Exception | InvalidEmail_Exception | AlreadyHasBina_Exception | FullStation_Exception e) {
				System.out.printf("Error (%s): %s\n", e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}
}

