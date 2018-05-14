package example.ws.handler;

import pt.ulisboa.tecnico.sdis.kerby.CipheredView;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Base64;
import java.util.Set;

public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {

	private Base64.Encoder encoder = Base64.getEncoder();
	private Base64.Decoder decoder = Base64.getDecoder();

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

				Name ticketName = envelope.createName("ticket", "", "http://ws.binas.org/");
				SOAPHeaderElement ticketElement = header.addHeaderElement(ticketName);
				CipheredView cTicket = (CipheredView) context.get("ticket");
				ticketElement.addTextNode(encoder.encodeToString(cTicket.getData()));

				Name authName = envelope.createName("auth", "", "http://ws.binas.org/");
				SOAPHeaderElement authElement = header.addHeaderElement(authName);
				CipheredView cAuth = (CipheredView) context.get("auth");
				authElement.addTextNode(encoder.encodeToString(cAuth.getData()));

				//TODO missing MAC
			} else {
				//TODO
			}
		} catch (SOAPException e) {
			e.printStackTrace();
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
