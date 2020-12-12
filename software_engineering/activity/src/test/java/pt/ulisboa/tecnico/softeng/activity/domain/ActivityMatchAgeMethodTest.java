package pt.ulisboa.tecnico.softeng.activity.domain;

import org.junit.Assert;
import org.junit.Test;

public class ActivityMatchAgeMethodTest extends RollbackTestAbstractClass {
	private static final int MIN_AGE = 25;
	private static final int MAX_AGE = 80;
	private static final int CAPACITY = 30;
	private Activity activity;

	@Override
	public void populate4Test() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		this.activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test
	public void success() {
		Assert.assertTrue(this.activity.matchAge((MAX_AGE - MIN_AGE) / 2));
	}

	@Test
	public void successEqualMinAge() {
		Assert.assertTrue(this.activity.matchAge(MIN_AGE));
	}

	public void lessThanMinAge() {
		Assert.assertFalse(this.activity.matchAge(MIN_AGE - 1));
	}

	public void successEqualMaxAge() {
		Assert.assertFalse(this.activity.matchAge(MAX_AGE));
	}

	public void greaterThanMaxAge() {
		Assert.assertFalse(this.activity.matchAge(MAX_AGE + 1));
	}

}
