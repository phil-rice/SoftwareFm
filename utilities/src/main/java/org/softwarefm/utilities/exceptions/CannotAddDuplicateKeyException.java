package org.softwarefm.utilities.exceptions;

public class CannotAddDuplicateKeyException extends RuntimeException {

	public CannotAddDuplicateKeyException() {
		super();
	}

	public CannotAddDuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotAddDuplicateKeyException(String message) {
		super(message);
	}

	public CannotAddDuplicateKeyException(Throwable cause) {
		super(cause);
	}

}
