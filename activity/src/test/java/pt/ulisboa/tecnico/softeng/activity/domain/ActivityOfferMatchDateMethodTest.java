package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityOfferMatchDateMethodTest extends RollbackTestAbstractClass {
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 23);

	private ActivityOffer offer;

	@Override
	public void populate4Test() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		Activity activity = new Activity(provider, "Bush Walking", 18, 80, 3);

		this.offer = new ActivityOffer(activity, this.begin, this.end, 30);
	}

	@Test
	public void success() {
		Assert.assertTrue(this.offer.matchDate(this.begin, this.end));
	}

	@Test(expected = ActivityException.class)
	public void nullBeginDate() {
		this.offer.matchDate(null, this.end);
	}

	@Test(expected = ActivityException.class)
	public void nullEndDate() {
		this.offer.matchDate(this.begin, null);
	}

	@Test
	public void beginPlusOne() {
		Assert.assertFalse(this.offer.matchDate(this.begin.plusDays(1), this.end));
	}

	@Test
	public void beginMinusOne() {
		Assert.assertFalse(this.offer.matchDate(this.begin.minusDays(1), this.end));
	}

	@Test
	public void endPlusOne() {
		Assert.assertFalse(this.offer.matchDate(this.begin, this.end.plusDays(1)));
	}

	@Test
	public void endMinusOne() {
		Assert.assertFalse(this.offer.matchDate(this.begin, this.end.minusDays(1)));
	}

}
