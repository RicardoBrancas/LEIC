package binas.ws.handler;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

import javax.crypto.Mac;
import javax.sound.midi.Soundbank;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

public class MACHandler implements SOAPHandler<SOAPMessageContext> {

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

			if (outbound) {
				//TODO
			} else {
				SOAPMessage message = context.getMessage();
				SOAPPart soapPart = message.getSOAPPart();
				SOAPEnvelope envelope = soapPart.getEnvelope();

				SOAPHeader header = envelope.getHeader();
				assert header != null; //TODO better handling

				Name ticketName = envelope.createName("ticket", "", "http://ws.binas.org/");
				SOAPElement ticketElement = (SOAPElement) header.getChildElements(ticketName).next();

				CipheredView cTicket = new CipheredView();
				cTicket.setData(decoder.decode(ticketElement.getValue()));
				Ticket t = new Ticket(cTicket, key);

				Key sessionKey = t.getKeyXY();

				// == Generate MAC from body ==

				String body = context.getMessage().getSOAPBody().getTextContent();
				Mac macInstance = Mac.getInstance("HmacSHA256");
				macInstance.init(sessionKey);
				String generatedMac = encoder.encodeToString(macInstance.doFinal(body.getBytes()));

				// == Get MAC from message ==

				Name macName = envelope.createName("mac", "", "http://ws.binas.org/");
				SOAPElement macElement = (SOAPElement) header.getChildElements(macName).next();
				String messageMac = macElement.getValue();

				return messageMac.equals(generatedMac); //Is this explicit?
			}

		} catch (SOAPException | KerbyException e) {
			e.printStackTrace(); //TODO treat exceptions
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
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
