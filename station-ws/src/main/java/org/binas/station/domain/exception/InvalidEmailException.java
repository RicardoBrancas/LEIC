package org.binas.station.domain.exception;

import org.binas.station.ws.InvalidEmail;
import org.binas.station.ws.InvalidEmail_Exception;

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
