package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;

public class BrokerConstructorMethodTest extends RollbackTestAbstractClass {

	@Override
	public void populate4Test() {
	}

	@Test
	public void success() {
		Broker broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);

		Assert.assertEquals(BROKER_CODE, broker.getCode());
		Assert.assertEquals(BROKER_NAME, broker.getName());
		Assert.assertEquals(0, broker.getAdventureSet().size());
		Assert.assertTrue(FenixFramework.getDomainRoot().getBrokerSet().contains(broker));

	}

	@Test
	public void nullCode() {
		try {
			new Broker(null, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void emptyCode() {
		try {
			new Broker("", BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void blankCode() {
		try {
			new Broker("  ", BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void uniqueCode() {
		Broker broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);

		try {
			new Broker(BROKER_CODE, "WeExploreX", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getBrokerSet().size());
			Assert.assertTrue(FenixFramework.getDomainRoot().getBrokerSet().contains(broker));
		}
	}

	@Test
	public void nullName() {
		try {
			new Broker(BROKER_CODE, null, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void emptyName() {
		try {
			new Broker(BROKER_CODE, "", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void blankName() {
		try {
			new Broker(BROKER_CODE, "    ", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void nullSellerNIF() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, null, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test

	public void emptySellerNIF() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, "    ", NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test

	public void uniqueSellerNIF() {
		new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, "123456789", BROKER_IBAN);
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void nullBuyerNIF() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, null, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void emptyBuyerNIF() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, "   ", BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void uniqueBuyerNIFOne() {
		new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		try {
			new Broker(BROKER_CODE, BROKER_NAME, "123456789", NIF_AS_BUYER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void uniqueBuyerSellerNIFTwo() {
		new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		try {
			new Broker(BROKER_CODE, BROKER_NAME, NIF_AS_BUYER, "123456789", BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void nullIBAN() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, null);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void emptyIBAN() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, "    ");
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

	@Test
	public void sellerNIFDifferentBuyerNIF() {
		try {
			new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, BROKER_NIF_AS_SELLER, BROKER_IBAN);
			Assert.fail();
		} catch (BrokerException be) {
			Assert.assertEquals(0, FenixFramework.getDomainRoot().getBrokerSet().size());
		}
	}

}
