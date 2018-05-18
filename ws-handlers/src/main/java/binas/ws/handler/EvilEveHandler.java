package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import binas.ws.handler.exception.MissingElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class EvilEveHandler extends BaseHandler {

	public static boolean eve = false;

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		if (!eve) return true;
		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			if (outbound) {
				NodeList emailElements = envelope.getElementsByTagName(EMAIL);
				if(emailElements.getLength() < 0)
					return true;

				Node emailElement = emailElements.item(0);
				if (emailElement == null)
					return true;

				emailElement.getFirstChild().setNodeValue("alice@A60.binas.org");
			}
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {

	}
}
