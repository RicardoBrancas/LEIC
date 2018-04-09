package org.binas.domain.exception;

import org.binas.ws.AlreadyHasBina;
import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;

public class AlreadyHasBinaException extends Exception {

	public AlreadyHasBinaException() {
		super();
	}

	public AlreadyHasBinaException(String message) {
		super(message);
	}

	public void throwWSException() throws AlreadyHasBina_Exception {
		throw new AlreadyHasBina_Exception(this.getMessage(), new AlreadyHasBina(), this);
	}
}
