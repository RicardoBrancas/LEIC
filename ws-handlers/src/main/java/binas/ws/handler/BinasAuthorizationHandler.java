package binas.ws.handler;

import org.w3c.dom.Node;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.util.Base64;
import java.util.Set;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {

	private static Key key;


	private Base64.Decoder decoder = Base64.getDecoder();


	public static void setKey(Key key) {
		BinasAuthorizationHandler.key = key;
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
				//TODO
			} else {

				// == MESSAGE EMAIL ==
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				Node emailElement = envelope.getElementsByTagName("email").item(0);
				String userInMessage = emailElement.getFirstChild().getNodeValue();


				// == TICKET ==
				SOAPHeader header = envelope.getHeader();
				assert header != null; //TODO better handling

				Node ticketElement = header.getElementsByTagName("ticket").item(0);
				CipheredView cTicket = new CipheredView();
				cTicket.setData(decoder.decode(ticketElement.getFirstChild().getNodeValue()));

				Ticket t = new Ticket(cTicket, key);

				String userInTicket = t.getX();

				return userInMessage.equals(userInTicket); //probably not very explicit

			}
		} catch (SOAPException e) { //TODO handle exception
			e.printStackTrace();
		} catch (KerbyException e) {
			e.printStackTrace();
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
