package binas.ws.handler;

import org.w3c.dom.Node;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {

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
			if (outbound) {
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				SOAPHeader header = envelope.getHeader();
				if (header == null)
					header = envelope.addHeader();

				// === TICKET ===

				Name ticketName = envelope.createName("ticket", "", "http://ws.binas.org/");
				SOAPHeaderElement ticketElement = header.addHeaderElement(ticketName);
				ticketElement.addTextNode(encoder.encodeToString(ticket.getData()));

				Date time = new Date();
				context.put("time", time);

				// == AUTH ==

				Auth auth = new Auth(user, time);
				CipheredView cAuth = auth.cipher(sessionKey);

				Name authName = envelope.createName("auth", "", "http://ws.binas.org/");
				SOAPHeaderElement authElement = header.addHeaderElement(authName);
				authElement.addTextNode(encoder.encodeToString(cAuth.getData()));

				// == K ==

				context.put("sessionKey", sessionKey);

			} else {
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				SOAPHeader header = envelope.getHeader();
				assert header != null; //TODO better handling

				Node timeElement = header.getElementsByTagName("time").item(0);
				CipheredView cTime = new CipheredView();
				cTime.setData(decoder.decode(timeElement.getFirstChild().getNodeValue()));

				RequestTime requestTime = new RequestTime(cTime, sessionKey);

				Date time = (Date) context.get("time");

				assert time.equals(requestTime.getTimeRequest()); //TODO better handling
			}
		} catch (SOAPException | KerbyException e) {
			e.printStackTrace(); //TODO treat exceptions
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
