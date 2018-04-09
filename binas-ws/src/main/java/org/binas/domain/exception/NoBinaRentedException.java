package org.binas.domain.exception;

import org.binas.ws.NoBinaRented;
import org.binas.ws.NoBinaRented_Exception;

public class NoBinaRentedException extends Exception {

	public NoBinaRentedException() {
		super();
	}

	public NoBinaRentedException(String message) {
		super(message);
	}

	public void throwWSException() throws NoBinaRented_Exception {
		throw new NoBinaRented_Exception(this.getMessage(), new NoBinaRented(), this);
	}
}
