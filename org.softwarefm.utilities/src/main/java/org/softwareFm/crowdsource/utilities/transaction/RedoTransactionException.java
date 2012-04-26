package org.softwareFm.crowdsource.utilities.transaction;

public class RedoTransactionException extends RuntimeException{

	public RedoTransactionException(String message) {
		super(message);
	}

	public RedoTransactionException() {
		super();
	}

	public RedoTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedoTransactionException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
