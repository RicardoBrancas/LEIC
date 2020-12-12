package pt.ulisboa.tecnico.softeng.activity.domain;

import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityConstructorMethodTest extends RollbackTestAbstractClass {
	private static final String IBAN = "IBAN";
	private static final String NIF = "NIF";
	private static final String PROVIDER_NAME = "Bush Walking";
	private static final int MIN_AGE = 25;
	private static final int MAX_AGE = 50;
	private static final int CAPACITY = 30;
	private ActivityProvider provider;

	@Override
	public void populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", NIF, IBAN);
	}

	@Test
	public void success() {
		Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(MIN_AGE, activity.getMinAge());
		Assert.assertEquals(MAX_AGE, activity.getMaxAge());
		Assert.assertEquals(CAPACITY, activity.getCapacity());
		Assert.assertEquals(0, activity.getActivityOfferSet().size());
		Assert.assertEquals(1, this.provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void nullProvider() {
		new Activity(null, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test(expected = ActivityException.class)
	public void nullProviderName() {
		new Activity(this.provider, null, MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test(expected = ActivityException.class)
	public void emptyProviderName() {
		new Activity(this.provider, "    ", MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test
	public void successMinAgeEqual18() {
		Activity activity = new Activity(this.provider, PROVIDER_NAME, 18, MAX_AGE, CAPACITY);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(18, activity.getMinAge());
		Assert.assertEquals(MAX_AGE, activity.getMaxAge());
		Assert.assertEquals(CAPACITY, activity.getCapacity());
		Assert.assertEquals(0, activity.getActivityOfferSet().size());
		Assert.assertEquals(1, this.provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void MinAgeLessThan18() {
		new Activity(this.provider, PROVIDER_NAME, 17, MAX_AGE, CAPACITY);
	}

	@Test
	public void successMaxAge99() {
		Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, 99, CAPACITY);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(MIN_AGE, activity.getMinAge());
		Assert.assertEquals(99, activity.getMaxAge());
		Assert.assertEquals(CAPACITY, activity.getCapacity());
		Assert.assertEquals(0, activity.getActivityOfferSet().size());
		Assert.assertEquals(1, this.provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void MaxAgeGreaterThan99() {
		new Activity(this.provider, PROVIDER_NAME, MIN_AGE, 100, CAPACITY);
	}

	@Test
	public void successMinAgeEqualMaxAge() {
		Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MIN_AGE, CAPACITY);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(MIN_AGE, activity.getMinAge());
		Assert.assertEquals(MIN_AGE, activity.getMaxAge());
		Assert.assertEquals(CAPACITY, activity.getCapacity());
		Assert.assertEquals(0, activity.getActivityOfferSet().size());
		Assert.assertEquals(1, this.provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void MinAgeGreaterThanMaxAge() {
		new Activity(this.provider, PROVIDER_NAME, MAX_AGE + 10, MAX_AGE, CAPACITY);
	}

	@Test(expected = ActivityException.class)
	public void MinAgeGreaterEqualMaxAgePlusOne() {
		new Activity(this.provider, PROVIDER_NAME, MAX_AGE + 1, MAX_AGE, CAPACITY);
	}

	@Test
	public void successCapacityOne() {
		Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 1);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(MIN_AGE, activity.getMinAge());
		Assert.assertEquals(MAX_AGE, activity.getMaxAge());
		Assert.assertEquals(1, activity.getCapacity());
		Assert.assertEquals(0, activity.getActivityOfferSet().size());
		Assert.assertEquals(1, this.provider.getActivitySet().size());
	}

	@Test(expected = ActivityException.class)
	public void zeroCapacity() {
		new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 0);
	}

}
