package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityOfferConstructorMethodTest extends RollbackTestAbstractClass {
	private static final int CAPACITY = 25;
	private static final int MAX_AGE = 50;
	private static final int MIN_AGE = 25;
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 21);
	private Activity activity;

	@Override
	public void populate4Test() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		this.activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test
	public void success() {
		ActivityOffer offer = new ActivityOffer(this.activity, this.begin, this.end, 30);

		assertEquals(this.begin, offer.getBegin());
		assertEquals(this.end, offer.getEnd());
		assertEquals(1, this.activity.getActivityOfferSet().size());
		assertEquals(0, offer.getNumberActiveOfBookings());
		assertEquals(30, offer.getPrice(), 0);
	}

	@Test(expected = ActivityException.class)
	public void nullActivity() {
		new ActivityOffer(null, this.begin, this.end, 30);
	}

	@Test(expected = ActivityException.class)
	public void nullbeginDate() {
		new ActivityOffer(this.activity, null, this.end, 30);
	}

	@Test(expected = ActivityException.class)
	public void nullEndDate() {
		new ActivityOffer(this.activity, this.begin, null, 30);
	}

	@Test
	public void successBeginDateEqualEndDate() {
		ActivityOffer offer = new ActivityOffer(this.activity, this.begin, this.begin, 30);

		Assert.assertEquals(this.begin, offer.getBegin());
		Assert.assertEquals(this.begin, offer.getEnd());
		Assert.assertEquals(1, this.activity.getActivityOfferSet().size());
		Assert.assertEquals(0, offer.getNumberActiveOfBookings());
	}

	@Test(expected = ActivityException.class)
	public void endDateImmediatelyBeforeBeginDate() {
		new ActivityOffer(this.activity, this.begin, this.begin.minusDays(1), 30);
	}

	@Test(expected = ActivityException.class)
	public void zeroAmount() {
		new ActivityOffer(this.activity, this.begin, this.end, 0);
	}

}
