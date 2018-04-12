package org.binas.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.cli.BinasClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;


/*
 * Base class of tests
 * Loads the properties in the file
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static BinasClient client;
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

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new BinasClient(uddiURL, wsName);
		} else {
			client = new BinasClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	@Before
	public void setup() {
		try {
			client.testInitStation("1", station1X,  station1Y,  station1Capacity, station1Prize);
			client.testInitStation("2", station2X,  station2Y,  station2Capacity, station2Prize);
			client.testInitStation("3", station3X,  station3Y,  station3Capacity, station3Prize);
			client.testInit(10);
		} catch (BadInit_Exception e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void cleanup() {
		client.testClear();
	}

}
