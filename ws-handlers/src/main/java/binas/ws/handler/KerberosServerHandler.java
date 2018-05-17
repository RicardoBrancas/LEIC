package binas.ws.handler;

import pt.ulisboa.tecnico.sdis.kerby.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger logger = Logger.getLogger(KerberosServerHandler.class.getName());

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

			if (outbound) {
				Key sessionKey = (Key) context.get("sessionKey");
				Date tRequest = (Date) context.get("tRequest");

				RequestTime requestTime = new RequestTime(tRequest);
				CipheredView cTime = requestTime.cipher(sessionKey);

				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				SOAPHeader header = envelope.getHeader();
				assert header != null; //TODO better handling

				Name timeName = envelope.createName("time", "", "http://ws.binas.org/");
				SOAPHeaderElement timeElement = header.addHeaderElement(timeName);
				timeElement.addTextNode(encoder.encodeToString(cTime.getData()));
			} else {
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				SOAPHeader header = envelope.getHeader();
				assert header != null; //TODO better handling

				// === TICKET ===

				Name ticketName = envelope.createName("ticket", "", "http://ws.binas.org/");
				SOAPElement ticketElement = (SOAPElement) header.getChildElements(ticketName).next();
				CipheredView cTicket = new CipheredView();
				cTicket.setData(decoder.decode(ticketElement.getValue()));
				Ticket t = new Ticket(cTicket, key);

				// = X =
				String user = t.getX(); //todo check user??

				// = Y =
				String me = t.getY();
				assert me.equals("binas@A60.binas.org"); //todo better handling

				// = T1 && T2 =
				Date currentDate = new Date();
				if (!(currentDate.after(t.getTime1()) && currentDate.before(t.getTime2()))) {
					logger.severe("Received ticket is expired!");
					throw new RuntimeException("Received ticket is expired!");
				}

				// = K =
				Key sessionKey = t.getKeyXY();
				context.put("sessionKey", sessionKey);
				// === AUTH ===

				Name authName = envelope.createName("auth", "", "http://ws.binas.org/");
				SOAPElement authElement = (SOAPElement) header.getChildElements(authName).next();
				CipheredView cAuth = new CipheredView();
				cAuth.setData(decoder.decode(authElement.getValue()));
				Auth auth = new Auth(cAuth, sessionKey);

				assert user.equals(auth.getX()); //TODO better handling
				assert currentDate.getTime() - auth.getTimeRequest().getTime() < TIME_TOLERANCE; //TODO better handling
				context.put("tRequest", auth.getTimeRequest());
			}

		} catch (SOAPException | KerbyException e) {
			e.printStackTrace(); //TODO treat exceptions
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
