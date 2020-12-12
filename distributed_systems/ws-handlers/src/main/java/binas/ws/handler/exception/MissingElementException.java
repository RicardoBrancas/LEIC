package binas.ws.handler.exception;

public class MissingElementException extends HandlerException {

	public MissingElementException(String element) {
		super("Missing " + element + " in message!");
	}
}
