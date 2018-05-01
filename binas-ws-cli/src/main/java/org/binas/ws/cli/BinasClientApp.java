package org.binas.ws.cli;

import org.binas.ws.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BinasClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BinasClientApp.class.getName()
					+ " wsURL OR uddiURL wsName");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		if (args.length == 1) {
			wsURL = args[0];
		} else /* args.length >= 2 */ {
			uddiURL = args[0];
			wsName = args[1];
		}

		System.out.println(BinasClientApp.class.getSimpleName() + " running");

		BinasClient binasClient = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			binasClient = new BinasClient(wsURL);

		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n",
					uddiURL, wsName);
			binasClient = new BinasClient(uddiURL, wsName);
		}

		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

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

						binasClient.activateUser(command[1]);
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

