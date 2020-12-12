package org.binas.domain.exception;

import org.binas.ws.InvalidStation;
import org.binas.ws.InvalidStation_Exception;

public class InvalidStationException extends Exception {

	public InvalidStationException() {
		super();
	}

	public InvalidStationException(String message) {
		super(message);
	}

	public void throwWSException() throws InvalidStation_Exception {
		throw new InvalidStation_Exception(this.getMessage(), new InvalidStation(), this);
	}
}
