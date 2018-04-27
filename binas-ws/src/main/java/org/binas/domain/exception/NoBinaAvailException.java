package org.binas.domain.exception;

import org.binas.ws.NoBinaAvail;
import org.binas.ws.NoBinaAvail_Exception;

public class NoBinaAvailException extends Exception {

	public NoBinaAvailException() {
		super();
	}

	public NoBinaAvailException(String message) {
		super(message);
	}

	public void throwWSException() throws NoBinaAvail_Exception {
		throw new NoBinaAvail_Exception(this.getMessage(), new NoBinaAvail(), this);
	}
}
