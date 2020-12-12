package org.binas.domain.exception;

import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;

public class EmailExistsException extends Exception {

	public EmailExistsException() {
		super();
	}

	public EmailExistsException(String message) {
		super(message);
	}

	public void throwWSException() throws EmailExists_Exception {
		throw new EmailExists_Exception(this.getMessage(), new EmailExists(), this);
	}
}
