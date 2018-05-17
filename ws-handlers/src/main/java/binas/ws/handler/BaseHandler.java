package binas.ws.handler;

import binas.ws.handler.exception.MissingElementException;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;

public abstract class BaseHandler implements SOAPHandler<SOAPMessageContext> {

	static final Name TICKET_NAME = NameImpl.create("ticket", "", "http://ws.binas.org/");

	static final Name AUTH_NAME = NameImpl.create("auth", "", "http://ws.binas.org/");

	static final Name TIME_NAME = NameImpl.create("time", "", "http://ws.binas.org/");

	static final Name MAC_NAME = NameImpl.create("mac", "", "http://ws.binas.org/");

	static final String EMAIL = "email";

	static final String REQUEST_TIME = "request_time";

	static final String USER = "user";

	static final String SESSION_KEY = "session_key";

	SOAPElement getHeaderElement(SOAPHeader header, Name name) {
		Iterator ticketElements = header.getChildElements(name);
		if (!ticketElements.hasNext())
			throw new MissingElementException(name.getLocalName());

		return (SOAPElement) ticketElements.next();
	}
}
