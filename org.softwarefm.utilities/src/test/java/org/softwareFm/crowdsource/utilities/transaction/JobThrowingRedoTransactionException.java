package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.functions.Functions.ConstantFunctionWithMemoryOfFroms;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public class JobThrowingRedoTransactionException extends ConstantFunctionWithMemoryOfFroms<IMonitor, String> {
	int tries;
	private final int maxTries;

	public JobThrowingRedoTransactionException(String object, int maxTries) {
		super(object);
		this.maxTries = maxTries;
	}

	@Override
	public String apply(IMonitor from) throws Exception {
		String result = super.apply(from);
		if (tries++ < maxTries)
			throw new RedoTransactionException();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	
}