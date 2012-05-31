package org.softwarefm.utilities.exceptions;

public class NotThreadSafeException extends RuntimeException{

	public NotThreadSafeException(String message) {
		super(message);
	}

}
