package binas.ws.handler;

import pt.ulisboa.tecnico.sdis.kerby.SessionKey;

import javax.crypto.Mac;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Logger;

public class MACHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger logger = Logger.getLogger(KerberosServerHandler.class.getName());

	private static Key key;

	public static void setKey(Key key) {
		MACHandler.key = key;
	}

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
			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			SOAPHeader header = envelope.getHeader();
			assert header != null; //TODO better handling

			if (outbound) {
				Name macName = envelope.createName("mac", "", "http://ws.binas.org/");
				SOAPHeaderElement macElement = header.addHeaderElement(macName);

				String body = context.getMessage().getSOAPBody().getTextContent();

				Mac macInstance = Mac.getInstance("HmacSHA256");
				Key sessionKey = (Key) context.get("sessionKey");
				macInstance.init(sessionKey);

				macElement.addTextNode(encoder.encodeToString(macInstance.doFinal(body.getBytes())));

			} else {

				Key sessionKey = (Key) context.get("sessionKey");

				// == Generate MAC from body ==

				String body = context.getMessage().getSOAPBody().getTextContent();
				Mac macInstance = Mac.getInstance("HmacSHA256");
				macInstance.init(sessionKey);
				String generatedMac = encoder.encodeToString(macInstance.doFinal(body.getBytes()));

				// == Get MAC from message ==

				Name macName = envelope.createName("mac", "", "http://ws.binas.org/");
				SOAPElement macElement = (SOAPElement) header.getChildElements(macName).next();
				String messageMac = macElement.getValue();

				if (!messageMac.equals(generatedMac)) {
					logger.severe("MAC not valid. This message has been tampered!");
					return false;
				}
			}

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
