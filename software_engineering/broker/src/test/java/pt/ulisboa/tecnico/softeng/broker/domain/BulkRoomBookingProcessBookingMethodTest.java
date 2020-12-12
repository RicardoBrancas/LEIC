package pt.ulisboa.tecnico.softeng.broker.domain;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class BulkRoomBookingProcessBookingMethodTest extends RollbackTestAbstractClass {
	private BulkRoomBooking bulk;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.bulk = new BulkRoomBooking(this.broker, NUMBER_OF_BULK, this.begin, this.end, NIF_AS_BUYER, IBAN_BUYER);
	}

	@Test
	public void success(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
			}
		};

		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void successTwice(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
				this.result = new HashSet<>(Arrays.asList("ref3", "ref4"));
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void cancelled(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
				this.result = new HotelException();
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void oneHotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HotelException();
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void maxHotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HotelException();
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(0, this.bulk.getReferences().size());
	}

	@Test
	public void maxMinusOneHotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new HotelException();
						} else {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						}
					}
				};
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void hotelExceptionValueIsResetBySuccess(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new HotelException();
						} else if (this.i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						} else if (this.i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new HotelException();
						} else {
							return new HashSet<>(Arrays.asList("ref3", "ref4"));
						}
					}
				};
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void hotelExceptionValueIsResetByRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new HotelException();
						} else if (this.i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new RemoteAccessException();
						} else if (this.i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
							throw new HotelException();
						} else {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						}
					}
				};
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void oneRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new RemoteAccessException();
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
			}
		};

		this.bulk.processBooking();
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void maxRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new RemoteAccessException();
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();

		assertEquals(0, this.bulk.getReferences().size());
	}

	@Test
	public void maxMinusOneRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						}
					}
				};
				this.result = new RemoteAccessException();
				this.times = BulkRoomBooking.MAX_REMOTE_ERRORS - 1;

				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void remoteExceptionValueIsResetBySuccess(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						} else if (this.i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else {
							return new HashSet<>(Arrays.asList("ref3", "ref4"));
						}
					}
				};
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();
		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void remoteExceptionValueIsResetByHotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(NUMBER_OF_BULK, arrival, departure, NIF_AS_BUYER, IBAN_BUYER,
						BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
				this.result = new Delegate() {
					int i = 0;

					Set<String> delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new HotelException();
						} else if (this.i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else {
							return new HashSet<>(Arrays.asList("ref1", "ref2"));
						}
					}
				};
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();
		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			this.bulk.processBooking();
		}
		this.bulk.processBooking();

		assertEquals(2, this.bulk.getReferences().size());
	}

}