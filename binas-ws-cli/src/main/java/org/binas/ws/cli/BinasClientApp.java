package org.binas.ws.cli;

import binas.ws.handler.KerberosClientHandler;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import org.binas.ws.*;
import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.SecureRandom;

public class BinasClientApp {

	private static SecureRandom random = new SecureRandom();
	private static Key clientKey;
	private static KerbyClient kerbyClient;

	private static void getTicket(String user) throws KerbyException, BadTicketRequest_Exception {
		long nonce = random.nextLong();

		SessionKeyAndTicketView sessionKeyAndTicket = kerbyClient.requestTicket(user, "binas@A60.binas.org", nonce, 25);

		CipheredView cSessionKey = sessionKeyAndTicket.getSessionKey();
		CipheredView cTicket = sessionKeyAndTicket.getTicket();

		SessionKey sessionKey = new SessionKey(cSessionKey, clientKey);

		KerberosClientHandler.setUser(user);
		KerberosClientHandler.setTicket(cTicket);
		KerberosClientHandler.setSessionKey(sessionKey.getKeyXY());
	}

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
		CipheredView cAuth;
		String user;

		System.out.printf("Creating kerby client for kerby server at %s\n", kerbyURL);
		kerbyClient = new KerbyClient(kerbyURL);
		while (true) {
			try {
				System.out.print("User: ");
				System.out.flush();
				user = input.readLine();

				System.out.print("Password: ");
				System.out.flush();
				String password = input.readLine();

				clientKey = SecurityHelper.generateKeyFromPassword(password);

				getTicket(user);


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

		System.out.println("Waiting for commands. Type 'help' if you need it...");
		String command[] = new String[0];
		boolean repeat = false;
		while (true) {
			if (!repeat) {
				System.out.print("> ");
				System.out.flush();
				command = input.readLine().split(" ");
			} else
				repeat = false;

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
						System.out.println("mkuser");
						System.out.println("read");
						System.out.println("rent <station>");
						System.out.println("return <station>");
						break;

					case "mkuser":

						UserView userView = binasClient.activateUser(user);
						if (userView != null)
							System.out.printf("(%s, %d, %b)\n", userView.getEmail(), userView.getCredit(), userView.isHasBina());
						else
							System.out.println("null");

						break;

					case "read":
						System.out.printf("credit: %d\n", binasClient.getCredit(user));
						break;

					case "rent":
						if (command.length < 2) {
							System.out.println("Syntax is rent <station>");
							continue;
						}

						binasClient.rentBina(command[1], user);
						break;

					case "return":
						if (command.length < 2) {
							System.out.println("Syntax is return <station>");
							continue;
						}

						binasClient.returnBina(command[1], user);
						break;

					default:
						System.out.println("Invalid command. Type 'help' if you need it");

				}
			} catch (UserNotExists_Exception | EmailExists_Exception | NoCredit_Exception | NoBinaRented_Exception | InvalidStation_Exception | NoBinaAvail_Exception | InvalidEmail_Exception | AlreadyHasBina_Exception | FullStation_Exception e) {
				System.out.printf("Error (%s): %s\n", e.getClass().getSimpleName(), e.getMessage());
			} catch (ServerSOAPFaultException e) {
				if (e.getMessage().contains("ticket is expired")) {
					getTicket(user);
					repeat = true;
				}
			}
		}
	}
}

