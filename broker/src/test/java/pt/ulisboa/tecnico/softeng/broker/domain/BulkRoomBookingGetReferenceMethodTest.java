package pt.ulisboa.tecnico.softeng.broker.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class BulkRoomBookingGetReferenceMethodTest extends RollbackTestAbstractClass {
	private BulkRoomBooking bulk;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.bulk = new BulkRoomBooking(this.broker, NUMBER_OF_BULK, this.begin, this.end, NIF_AS_BUYER, CLIENT_IBAN);
		new Reference(this.bulk, REF_ONE);
		new Reference(this.bulk, REF_TWO);
	}

	@Test
	public void successSINGLE(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new Delegate() {
					RestRoomBookingData delegate() {
						RestRoomBookingData roomBookingData = new RestRoomBookingData();
						roomBookingData.setRoomType(SINGLE);
						return roomBookingData;
					}
				};
			}
		};

		this.bulk.getReference(SINGLE);

		assertEquals(1, this.bulk.getReferences().size());
	}

	@Test
	public void successDOUBLE(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new Delegate() {
					RestRoomBookingData delegate() {
						RestRoomBookingData roomBookingData = new RestRoomBookingData();
						roomBookingData.setRoomType(DOUBLE);
						return roomBookingData;
					}
				};
			}
		};

		this.bulk.getReference(DOUBLE);

		assertEquals(1, this.bulk.getReferences().size());
	}

	@Test
	public void hotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new HotelException();
				this.times = 2;
			}
		};

		assertNull(this.bulk.getReference(DOUBLE));

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void remoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new RemoteAccessException();
				this.times = 2;
			}
		};

		assertNull(this.bulk.getReference(DOUBLE));

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void maxRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new RemoteAccessException();
				this.times = BulkRoomBooking.MAX_REMOTE_ERRORS;
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}
		assertNull(this.bulk.getReference(DOUBLE));

		assertEquals(2, this.bulk.getReferences().size());
	}

	@Test
	public void maxMinusOneRemoteException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					RestRoomBookingData delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else {
							RestRoomBookingData roomBookingData = new RestRoomBookingData();
							roomBookingData.setRoomType(DOUBLE);
							return roomBookingData;
						}
					}
				};
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}
		this.bulk.getReference(DOUBLE);

		assertEquals(1, this.bulk.getReferences().size());
	}

	@Test
	public void remoteExceptionValueIsResetBySuccess(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					RestRoomBookingData delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							RestRoomBookingData roomBookingData = new RestRoomBookingData();
							roomBookingData.setRoomType(DOUBLE);
							return roomBookingData;
						} else {
							throw new RemoteAccessException();
						}
					}
				};
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}

		this.bulk.getReference(DOUBLE);

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}

		assertEquals(1, this.bulk.getReferences().size());
	}

	@Test
	public void remoteExceptionValueIsResetByHotelException(@Mocked final HotelInterface roomInterface) {
		new Expectations() {
			{
				HotelInterface.getRoomBookingData(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					RestRoomBookingData delegate() {
						this.i++;
						if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new RemoteAccessException();
						} else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
							throw new HotelException();
						} else {
							throw new RemoteAccessException();
						}
					}
				};
			}
		};

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}

		this.bulk.getReference(DOUBLE);

		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
			assertNull(this.bulk.getReference(DOUBLE));
		}

		assertEquals(2, this.bulk.getReferences().size());
	}

}