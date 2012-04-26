package org.softwareFm.crowdsource.utilities.transaction;

import java.text.MessageFormat;
import java.util.List;

import org.softwareFm.crowdsource.utilities.constants.UtilityMessages;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class PoisonedTransactionException extends RuntimeException {

	private final List<RedoTransactionException> exceptions;

	public PoisonedTransactionException(IFunction1<?, ?> job, List<RedoTransactionException> exceptions) {
		super(MessageFormat.format(UtilityMessages.poisonedTransactionException, job, exceptions.size(), exceptions));
		this.exceptions = exceptions;
	}

	public List<RedoTransactionException> getRedoTransactionExceptions() {
		return exceptions;
	}

	@Override
	public String toString() {
		return "PoisonedTransactionException [exceptions=" + exceptions.size() + "]";
	}
	

}
