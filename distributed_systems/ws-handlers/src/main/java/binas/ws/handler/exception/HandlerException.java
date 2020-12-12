package binas.ws.handler.exception;

import java.util.logging.Logger;

public class HandlerException extends RuntimeException {
	private static final Logger logger = Logger.getLogger(HandlerException.class.getName());

	public HandlerException(String message) {
		super(message);
		logger.warning("Exception in handler: " + message);
	}

	public HandlerException(Throwable cause) {
		super(cause);
		logger.warning("Exception in handler: " + cause.getMessage());
	}
}
