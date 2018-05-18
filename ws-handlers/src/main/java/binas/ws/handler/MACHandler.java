package binas.ws.handler;

import binas.ws.handler.exception.HandlerException;
import binas.ws.handler.exception.MissingElementException;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;

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

/**
 * Add MAC to outgoing messages and verifies MAC on incoming messages.
 */
public class MACHandler extends BaseHandler {

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
			// server: This is the first thing to be executed when a response is sent

			SOAPMessage message = context.getMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			SOAPHeader header = envelope.getHeader();
			if (header == null)
				throw new MissingElementException("header");

			/*
			 * Outbound: Generate MAC from user authentication and message body content
			 */
			if (outbound) {

				// Mac: https://docs.oracle.com/javase/8/docs/api/javax/crypto/Mac.html
				Mac macInstance = Mac.getInstance("HmacSHA256");
				Key sessionKey = (Key) context.get(SESSION_KEY);
				macInstance.init(sessionKey);

				// == Add auth to MAC ==
				Auth auth = (Auth) context.get(AUTH);
				String authString = auth.authToString();
				macInstance.update(authString.getBytes());

				// == Add body to MAC ==
				SOAPBody body = message.getSOAPBody();
				String bodyString = body.getTextContent();
				macInstance.update(bodyString.getBytes());

				SOAPHeaderElement macElement = header.addHeaderElement(MAC_NAME);
				macElement.addTextNode(encoder.encodeToString(macInstance.doFinal()));

			}
			/*
			 * Inbound: Generate MAC from user authentication and message body content
			 * and compare it with MAC in message
			 */
			else {
				// server: This is the last thing to be executed when a message is received

				Key sessionKey = (Key) context.get(SESSION_KEY);
				Mac macInstance = Mac.getInstance("HmacSHA256");
				macInstance.init(sessionKey);


				// == Generate MAC from body and auth in message ==

				// get auth:
				SOAPElement authElement = getHeaderElement(header, AUTH_NAME);
				CipheredView cAuth = new CipheredView();
				cAuth.setData(decoder.decode(authElement.getValue()));
				Auth auth = new Auth(cAuth, sessionKey);
				String authString = auth.authToString();
				macInstance.update(authString.getBytes());

				// get body:
				SOAPBody body = message.getSOAPBody();
				String bodyString = body.getTextContent();
				macInstance.update(bodyString.getBytes());

				String generatedMac = encoder.encodeToString(macInstance.doFinal());


				// == Get MAC from message ==
				SOAPElement macElement = getHeaderElement(header, MAC_NAME);
				String messageMac = macElement.getValue();

				if (!messageMac.equals(generatedMac))
					throw new HandlerException("MAC not valid. This message has been tampered!");
			}

		} catch (InvalidKeyException | NoSuchAlgorithmException | SOAPException e) {
			throw new HandlerException(e);
		} catch (KerbyException e) {
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
