package org.softwareFm.crowdsource.utilities.exceptions;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.utililties.aggregators.IAggregator;

public class AggregateException extends RuntimeException {

	private final List<Exception> exceptions;

	public AggregateException(List<Exception> exceptions) {
		super(Iterables.aggregate(exceptions, IAggregator.Utils.join(new IFunction1<Exception, String>() {
			@Override
			public String apply(Exception from) throws Exception {
				return from.getClass() + "/" + from.getMessage();
			}
		}, ",")));
		this.exceptions = exceptions;
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}

}
