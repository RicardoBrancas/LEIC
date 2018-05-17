package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import binas.ws.handler.exception.MissingElementException;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

public class KerberosClientHandler extends BaseHandler {

	private static String user;
	private static CipheredView ticket;
	private static Key sessionKey;

	private Base64.Encoder encoder = Base64.getEncoder();
	private Base64.Decoder decoder = Base64.getDecoder();

	public static void setUser(String user) {
		KerberosClientHandler.user = user;
	}

	public static void setTicket(CipheredView ticket) {
		KerberosClientHandler.ticket = ticket;
	}

	public static void setSessionKey(Key sessionKey) {
		KerberosClientHandler.sessionKey = sessionKey;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			if (outbound) {
				SOAPHeader header = envelope.getHeader();
				if (header == null)
					header = envelope.addHeader();

				// === TICKET ===

				SOAPHeaderElement ticketElement = header.addHeaderElement(TICKET_NAME);
				ticketElement.addTextNode(encoder.encodeToString(ticket.getData()));

				Date time = new Date();
				context.put(REQUEST_TIME, time); //will be used later to validate response

				// == AUTH ==

				Auth auth = new Auth(user, time);
				CipheredView cAuth = auth.cipher(sessionKey);
				SOAPHeaderElement authElement = header.addHeaderElement(AUTH_NAME);
				authElement.addTextNode(encoder.encodeToString(cAuth.getData()));

				// == K ==

				context.put(SESSION_KEY, sessionKey);
				context.put(AUTH, auth);

			} else {
				SOAPHeader header = envelope.getHeader();
				if (header == null)
					throw new MissingElementException("header");

				SOAPElement timeElement = getHeaderElement(header, TIME_NAME);
				CipheredView cTime = new CipheredView();
				cTime.setData(decoder.decode(timeElement.getFirstChild().getNodeValue()));

				RequestTime requestTime = new RequestTime(cTime, sessionKey);

				Date time = (Date) context.get(REQUEST_TIME); //retrieve previously saved time

				if (!time.equals(requestTime.getTimeRequest()))
					throw new HandlerException("Time in response is different from request.");
			}
		} catch (SOAPException | KerbyException e) {
			throw new HandlerException(e);
		}

		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		//TODO ?
		return true;
	}

	@Override
	public void close(MessageContext context) {

	}
}
