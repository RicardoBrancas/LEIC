package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import binas.ws.handler.exception.MissingElementException;

import javax.crypto.Mac;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

public class MACHandler extends BaseHandler {

	private Base64.Encoder encoder = Base64.getEncoder();

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {


		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			// server: This is the first thing to be executed when a response is sent

			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			SOAPHeader header = envelope.getHeader();
			if (header == null)
				throw new MissingElementException("header");

			if (outbound) {
				SOAPHeaderElement macElement = header.addHeaderElement(MAC_NAME);

				String body = message.getSOAPBody().getTextContent();

				Mac macInstance = Mac.getInstance("HmacSHA256");
				Key sessionKey = (Key) context.get(SESSION_KEY);
				macInstance.init(sessionKey);

				macElement.addTextNode(encoder.encodeToString(macInstance.doFinal(body.getBytes())));

			} else {
				// server: This is the last thing to be executed when a message is received

				Key sessionKey = (Key) context.get(SESSION_KEY);

				// == Generate MAC from body ==

				String body = message.getSOAPBody().getTextContent();
				Mac macInstance = Mac.getInstance("HmacSHA256");
				macInstance.init(sessionKey);
				String generatedMac = encoder.encodeToString(macInstance.doFinal(body.getBytes()));

				// == Get MAC from message ==

				SOAPElement macElement = getHeaderElement(header, MAC_NAME);
				String messageMac = macElement.getValue();

				if (!messageMac.equals(generatedMac))
					throw new HandlerException("MAC not valid. This message has been tampered!");
			}

		} catch (InvalidKeyException | NoSuchAlgorithmException | SOAPException e) {
			throw new HandlerException(e);
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
