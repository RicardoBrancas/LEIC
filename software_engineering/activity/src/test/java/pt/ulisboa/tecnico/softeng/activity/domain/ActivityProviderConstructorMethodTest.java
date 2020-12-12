package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProviderConstructorMethodTest extends RollbackTestAbstractClass {
	private static final String PROVIDER_CODE = "XtremX";
	private static final String PROVIDER_NAME = "Adventure++";
	private static final String IBAN = "IBAN";
	private static final String NIF = "NIF";

	@Override
	public void populate4Test() {
	}

	@Test
	public void success() {
		ActivityProvider provider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

		Assert.assertEquals(PROVIDER_NAME, provider.getName());
		Assert.assertTrue(provider.getCode().length() == ActivityProvider.CODE_SIZE);
		Assert.assertEquals(1, FenixFramework.getDomainRoot().getActivityProviderSet().size());
		Assert.assertEquals(0, provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void nullCode() {
		new ActivityProvider(null, PROVIDER_NAME, NIF, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void emptyCode() {
		new ActivityProvider("      ", PROVIDER_NAME, NIF, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void nullName() {
		new ActivityProvider(PROVIDER_CODE, null, NIF, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void emptyName() {
		new ActivityProvider(PROVIDER_CODE, "    ", NIF, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void fiveCharCode() {
		new ActivityProvider("12345", PROVIDER_NAME, NIF, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void sevenCharCode() {
		new ActivityProvider("1234567", PROVIDER_NAME, NIF, IBAN);
	}

	@Test
	public void noteUniqueCode() {
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

		try {
			new ActivityProvider(PROVIDER_CODE, "Hello", NIF + "2", IBAN);
			fail();
		} catch (ActivityException ae) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getActivityProviderSet().size());
		}
	}

	@Test
	public void noteUniqueName() {
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

		try {
			new ActivityProvider("123456", PROVIDER_NAME, NIF + "2", IBAN);
			fail();
		} catch (ActivityException ae) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getActivityProviderSet().size());
		}
	}

	@Test
	public void noteUniqueNIF() {
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

		try {
			new ActivityProvider("123456", "jdgdsk", NIF, IBAN);
			fail();
		} catch (ActivityException ae) {
			Assert.assertEquals(1, FenixFramework.getDomainRoot().getActivityProviderSet().size());
		}
	}

	@Test(expected = ActivityException.class)
	public void nullNIF() {
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, null, IBAN);
	}

	@Test(expected = ActivityException.class)
	public void emptyNIF() {
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, "   ", IBAN);
	}

}
