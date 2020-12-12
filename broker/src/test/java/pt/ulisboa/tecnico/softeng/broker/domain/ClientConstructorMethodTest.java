package pt.ulisboa.tecnico.softeng.broker.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;

public class ClientConstructorMethodTest extends RollbackTestAbstractClass {

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
	}

	@Test
	public void success() {
		Client client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);

		Assert.assertEquals(CLIENT_IBAN, client.getIban());
		Assert.assertEquals(CLIENT_NIF, client.getNif());
		Assert.assertEquals(AGE, client.getAge());
	}

	@Test(expected = BrokerException.class)
	public void nullBroker() {
		new Client(null, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
	}

	@Test(expected = BrokerException.class)
	public void nullIBAN() {
		new Client(this.broker, null, CLIENT_NIF, DRIVING_LICENSE, AGE);
	}

	@Test(expected = BrokerException.class)
	public void emptyOrBlankIBAN() {
		new Client(this.broker, "   ", CLIENT_NIF, DRIVING_LICENSE, AGE);
	}

	@Test(expected = BrokerException.class)
	public void nullNIF() {
		new Client(this.broker, CLIENT_IBAN, null, DRIVING_LICENSE, AGE);
	}

	@Test(expected = BrokerException.class)
	public void emptyOrBlankNIF() {
		new Client(this.broker, CLIENT_IBAN, "    ", DRIVING_LICENSE, AGE);
	}

	@Test(expected = BrokerException.class)
	public void negativeAge() {
		new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, -1);
	}

	@Test
	public void clientExistsWithSameIBAN() {
		new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		new Client(this.broker, CLIENT_IBAN, "OTHER_NIF", DRIVING_LICENSE + "1", AGE);
	}

	@Test
	public void clientExistsWithSameNIF() {
		Client client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		try {
			new Client(this.broker, "OTHER_IBAN", CLIENT_NIF, DRIVING_LICENSE + "1", AGE);
			fail();
		} catch (BrokerException br) {
			assertEquals(client, this.broker.getClientByNIF(CLIENT_NIF));
		}
	}

	@Test
	public void nullDrivingLicense() {
		Client client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, null, AGE);

		Assert.assertEquals(CLIENT_IBAN, client.getIban());
		Assert.assertEquals(CLIENT_NIF, client.getNif());
		Assert.assertEquals(AGE, client.getAge());
		Assert.assertNull(client.getDrivingLicense());
	}

	@Test(expected = BrokerException.class)
	public void emptyOrBlankDrivingLicense() {
		new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, "      ", AGE);
	}

}
