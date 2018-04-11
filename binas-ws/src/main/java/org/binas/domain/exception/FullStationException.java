package org.binas.domain.exception;

import org.binas.ws.FullStation;
import org.binas.ws.FullStation_Exception;

public class FullStationException extends Exception {

	public FullStationException() {
		super();
	}

	public FullStationException(String message) {
		super(message);
	}

	public void throwWSException() throws FullStation_Exception {
		throw new FullStation_Exception(this.getMessage(), new FullStation(), this);
	}
}
