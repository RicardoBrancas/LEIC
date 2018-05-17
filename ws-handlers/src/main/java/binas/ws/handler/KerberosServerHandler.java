package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import binas.ws.handler.exception.MissingElementException;
import pt.ulisboa.tecnico.sdis.kerby.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

public class KerberosServerHandler extends BaseHandler {

	private static final long TIME_TOLERANCE = 500;

	private static Key key;

	private Base64.Encoder encoder = Base64.getEncoder();
	private Base64.Decoder decoder = Base64.getDecoder();

	public static void setKey(Key key) {
		KerberosServerHandler.key = key;
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

			SOAPHeader header = envelope.getHeader();
			if (header == null)
				throw new MissingElementException("header");

			if (outbound) {
				// server: This is the last thing to be executed when a response is sent

				Key sessionKey = (Key) context.get(SESSION_KEY);
				Date tRequest = (Date) context.get(REQUEST_TIME);
				Auth auth = (Auth) context.get(AUTH);

				CipheredView cAuth = auth.cipher(sessionKey);
				SOAPHeaderElement authElement = header.addHeaderElement(AUTH_NAME);
				authElement.addTextNode(encoder.encodeToString(cAuth.getData()));

				RequestTime requestTime = new RequestTime(tRequest);
				CipheredView cTime = requestTime.cipher(sessionKey);
				SOAPHeaderElement timeElement = header.addHeaderElement(TIME_NAME);
				timeElement.addTextNode(encoder.encodeToString(cTime.getData()));

			} else {
				// server: This is the first thing to be executed when a message is received

				// === TICKET ===

				SOAPElement ticketElement = getHeaderElement(header, TICKET_NAME);
				CipheredView cTicket = new CipheredView();
				cTicket.setData(decoder.decode(ticketElement.getValue()));
				Ticket t = new Ticket(cTicket, key);

				// = X =
				String user = t.getX();
				context.put(USER, user); //save user for later

				// = Y =
				String me = t.getY();
				if (!me.equals("binas@A60.binas.org"))
					throw new HandlerException("Y in ticket is not us! Rejecting.");

				// = T1 && T2 =
				Date currentDate = new Date();
				if (!(currentDate.after(t.getTime1()) && currentDate.before(t.getTime2())))
					throw new HandlerException("Received ticket is expired!");

				// = K =
				Key sessionKey = t.getKeyXY();
				context.put(SESSION_KEY, sessionKey); //save sessionKey for later

				// === AUTH ===

				SOAPElement authElement = getHeaderElement(header, AUTH_NAME);
				CipheredView cAuth = new CipheredView();
				cAuth.setData(decoder.decode(authElement.getValue()));
				Auth auth = new Auth(cAuth, sessionKey);

				if (!user.equals(auth.getX()))
					throw new HandlerException("Users in ticket and auth are different!");

				if (!((currentDate.getTime() - auth.getTimeRequest().getTime()) < TIME_TOLERANCE))
					throw new HandlerException("Auth is too old!");

				context.put(REQUEST_TIME, auth.getTimeRequest()); //save time for later
				context.put(AUTH, auth);
			}

		} catch (SOAPException | KerbyException e) {
			throw new HandlerException(e);
		}

		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {

	}
}
