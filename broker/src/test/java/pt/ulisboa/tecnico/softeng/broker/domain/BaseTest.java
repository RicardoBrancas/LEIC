package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;

public abstract class BaseTest {
	protected static final String BROKER_CODE = "BR01";
	protected static final String BROKER_NAME = "WeExplore";
	protected static final String BROKER_IBAN = "BROKER_IBAN";
	protected static final String BROKER_NIF_AS_BUYER = "buyerNIF";
	protected static final String NIF_AS_BUYER = "buyerNIF";
	protected static final String BROKER_NIF_AS_SELLER = "sellerNIF";
	protected static final String IBAN_BUYER = "IBAN";
	protected static final String NIF_CUSTOMER = "123456789";
	protected static final String OTHER_NIF = "987654321";
	protected static final String CLIENT_NIF = "123456789";
	protected static final String DRIVING_LICENSE = "IMT1234";
	protected static final int AGE = 20;
	protected static final int NUMBER_OF_BULK = 20;
	protected static final double MARGIN = 0.3;
	protected static final String CLIENT_IBAN = "BK011234567";

	protected static final String REFERENCE = "REFERENCE";
	protected static final String PAYMENT_CONFIRMATION = "PaymentConfirmation";
	protected static final String PAYMENT_CANCELLATION = "PaymentCancellation";
	protected static final String ACTIVITY_CONFIRMATION = "ActivityConfirmation";
	protected static final String ACTIVITY_CANCELLATION = "ActivityCancellation";
	protected static final String ROOM_CONFIRMATION = "RoomConfirmation";
	protected static final String ROOM_CANCELLATION = "RoomCancellation";
	protected static final String RENTING_CONFIRMATION = "RentingConfirmation";
	protected static final String RENTING_CANCELLATION = "RentingCancellation";
	protected static final String INVOICE_REFERENCE = "InvoiceReference";
	protected static final String INVOICE_DATA = "InvoiceData";
	protected static final String SINGLE = "SINGLE";
	protected static final String DOUBLE = "DOUBLE";
	protected static final String REF_ONE = "ref1";
	protected static final String REF_TWO = "ref2";

	protected final LocalDate begin = new LocalDate(2016, 12, 19);
	protected final LocalDate end = new LocalDate(2016, 12, 21);
	protected static final LocalDate arrival = new LocalDate(2016, 12, 19);
	protected static final LocalDate departure = new LocalDate(2016, 12, 21);

	protected Broker broker;
	protected Client client;
	protected Adventure adventure;

}
