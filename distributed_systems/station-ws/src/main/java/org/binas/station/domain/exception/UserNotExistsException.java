package org.binas.station.domain.exception;

import org.binas.station.ws.UserNotExists;
import org.binas.station.ws.UserNotExists_Exception;

public class UserNotExistsException extends Exception {

	public UserNotExistsException() {
		super();
	}

	public UserNotExistsException(String message) {
		super(message);
	}

	public void throwWSException() throws UserNotExists_Exception {
		throw new UserNotExists_Exception(this.getMessage(), new UserNotExists(), this);
	}

}
