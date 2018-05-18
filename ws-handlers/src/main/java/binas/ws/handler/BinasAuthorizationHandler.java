package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public class BinasAuthorizationHandler extends BaseHandler {

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (!outbound) {
				// server: This is the second thing to be executed when a message is received

				// == MESSAGE EMAIL ==
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				Node emailElement = envelope.getElementsByTagName(EMAIL).item(0);
				String userInMessage = emailElement.getFirstChild().getNodeValue();

				String userInTicket = (String) context.get(USER_EMAIL); // Retrieve previously saved user

				if (!userInMessage.equals(userInTicket))
					throw new HandlerException(String.format("Permission denied. User '%s' can't access account '%s'", userInTicket, userInMessage));
			}
		} catch (SOAPException e) {
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
