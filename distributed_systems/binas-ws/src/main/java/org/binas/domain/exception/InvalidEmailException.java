package org.binas.domain.exception;

import org.binas.ws.InvalidEmail;
import org.binas.ws.InvalidEmail_Exception;

public class InvalidEmailException extends Exception {

	public InvalidEmailException() {
		super();
	}

	public InvalidEmailException(String message) {
		super(message);
	}

	public void throwWSException() throws InvalidEmail_Exception {
		throw new InvalidEmail_Exception(this.getMessage(), new InvalidEmail(), this);
	}
}
