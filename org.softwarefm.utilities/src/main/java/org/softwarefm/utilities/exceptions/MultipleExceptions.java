package org.softwarefm.utilities.exceptions;

import java.util.List;

public class MultipleExceptions extends RuntimeException {

	private final List<Throwable> throwables;

	public MultipleExceptions(List<Throwable> throwables) {
		this.throwables = throwables;
	}

	public List<Throwable> getCauses() {
		return throwables;
	}
}
