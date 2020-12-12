package org.binas.domain.exception;

import org.binas.ws.NoCredit;
import org.binas.ws.NoCredit_Exception;

public class NoCreditException extends Exception {

	public NoCreditException() {
		super();
	}

	public NoCreditException(String message) {
		super(message);
	}

	public void throwWSException() throws NoCredit_Exception {
		throw new NoCredit_Exception(this.getMessage(), new NoCredit(), this);
	}
}
