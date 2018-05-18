package org.binas.ws.it;

import binas.ws.handler.KerberosClientHandler;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.cli.BinasClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;


/*
 * Base class of tests
 * Loads the properties in the file
 */
public class BaseIT {
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	protected static BinasClient client;
	protected static KerbyClient kerbyClient;
	protected static String stationName = "A60_Station";
	protected static int station1X = 22;
	protected static int station1Y = 7;
	protected static int station1Capacity = 6;
	protected static int station1Prize = 2;
	protected static int station2X = 80;
	protected static int station2Y = 20;
	protected static int station2Capacity = 12;
	protected static int station2Prize = 1;
	protected static int station3X = 50;
	protected static int station3Y = 50;
	protected static int station3Capacity = 20;
	protected static int station3Prize = 0;
	private final SecureRandom random = new SecureRandom();
	String email = "alice@A60.binas.org";
	String password = "LgzpKrs7F";

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");


		kerbyClient = new KerbyClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new BinasClient(uddiURL, wsName);
		} else {
			client = new BinasClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	void getTicket(String user, Key clientKey) throws KerbyException, BadTicketRequest_Exception {
		final long nonce = random.nextLong();

		SessionKeyAndTicketView sessionKeyAndTicket = kerbyClient.requestTicket(user, "binas@A60.binas.org", nonce, 300);

		CipheredView cSessionKey = sessionKeyAndTicket.getSessionKey();
		CipheredView cTicket = sessionKeyAndTicket.getTicket();

		SessionKey sessionKey = new SessionKey(cSessionKey, clientKey);

		KerberosClientHandler.setUser(user);
		KerberosClientHandler.setTicket(cTicket);
		KerberosClientHandler.setSessionKey(sessionKey.getKeyXY());
	}

	@Before
	public void setup() {
		try {
			Key clientKey = SecurityHelper.generateKeyFromPassword(password);
			getTicket(email, clientKey);
			client.testInitStation("1", station1X, station1Y, station1Capacity, station1Prize);
			client.testInitStation("2", station2X, station2Y, station2Capacity, station2Prize);
			client.testInitStation("3", station3X, station3Y, station3Capacity, station3Prize);
			client.testInit(10);
		} catch (BadInit_Exception | KerbyException | BadTicketRequest_Exception | InvalidKeySpecException |
				NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void cleanup() {
		client.testClear();
	}

}
