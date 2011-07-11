package org.arc4eclipse.utilities.exceptions;

public class WrappedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WrappedException(Throwable e) {
		super(e);
	}

	public static RuntimeException wrap(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		return new WrappedException(e);
	}

	public static Throwable unwrap(Throwable e) {
		if (e instanceof WrappedException)
			return unwrap(((WrappedException) e).getCause());
		else
			return e;
	}

}
