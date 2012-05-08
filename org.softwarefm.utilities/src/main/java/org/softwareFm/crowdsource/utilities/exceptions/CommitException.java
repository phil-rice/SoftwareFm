package org.softwareFm.crowdsource.utilities.exceptions;

import java.util.List;

public class CommitException extends AggregateException{

	public CommitException(List<Exception> exceptions) {
		super(exceptions);
	}

}
