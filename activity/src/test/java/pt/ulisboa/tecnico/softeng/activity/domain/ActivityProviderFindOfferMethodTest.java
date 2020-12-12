package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

@RunWith(JMockit.class)
public class ActivityProviderFindOfferMethodTest extends RollbackTestAbstractClass {
	private static final int MIN_AGE = 25;
	private static final int MAX_AGE = 80;
	private static final int CAPACITY = 25;
	private static final int AGE = 40;
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 21);

	private ActivityProvider provider;
	private Activity activity;
	private ActivityOffer offer;

	@Override
	public void populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		this.activity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);

		this.offer = new ActivityOffer(this.activity, this.begin, this.end, 30);
	}

	@Test
	public void success() {
		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, AGE);

		Assert.assertEquals(1, offers.size());
		Assert.assertTrue(offers.contains(this.offer));
	}

	@Test(expected = ActivityException.class)
	public void nullBeginDate() {
		this.provider.findOffer(null, this.end, AGE);

	}

	@Test(expected = ActivityException.class)
	public void nullEndDate() {
		this.provider.findOffer(this.begin, null, AGE);

	}

	@Test
	public void successAgeEqualMin() {
		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, MIN_AGE);

		Assert.assertEquals(1, offers.size());
		Assert.assertTrue(offers.contains(this.offer));
	}

	@Test
	public void AgeMinusOneThanMinimal() {
		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, MIN_AGE - 1);

		Assert.assertTrue(offers.isEmpty());
	}

	@Test
	public void successAgeEqualMax() {
		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, MAX_AGE);

		Assert.assertEquals(1, offers.size());
		Assert.assertTrue(offers.contains(this.offer));
	}

	@Test
	public void AgePlusOneThanMinimal() {
		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, MAX_AGE + 1);

		Assert.assertTrue(offers.isEmpty());
	}

	@Test
	public void emptyActivitySet() {
		ActivityProvider otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN");

		List<ActivityOffer> offers = otherProvider.findOffer(this.begin, this.end, AGE);

		Assert.assertTrue(offers.isEmpty());
	}

	@Test
	public void emptyActivityOfferSet() {
		ActivityProvider otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN");
		new Activity(otherProvider, "Bush Walking", 18, 80, 25);

		List<ActivityOffer> offers = otherProvider.findOffer(this.begin, this.end, AGE);

		Assert.assertTrue(offers.isEmpty());
	}

	@Test
	public void twoMatchActivityOffers() {
		new ActivityOffer(this.activity, this.begin, this.end, 30);

		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, AGE);

		Assert.assertEquals(2, offers.size());
	}

	@Test
	public void oneMatchActivityOfferAndOneNotMatch() {
		new ActivityOffer(this.activity, this.begin, this.end.plusDays(1), 30);

		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, AGE);

		Assert.assertEquals(1, offers.size());
	}

	@Test
	public void oneMatchActivityOfferAndOtherNoCapacity() {
		Activity otherActivity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, 1);

		ActivityOffer otherActivityOffer = new ActivityOffer(otherActivity, this.begin, this.end, 30);
		new Booking(this.provider, otherActivityOffer, "123456789", "IBAN");

		List<ActivityOffer> offers = this.provider.findOffer(this.begin, this.end, AGE);

		Assert.assertEquals(1, offers.size());
	}

}
