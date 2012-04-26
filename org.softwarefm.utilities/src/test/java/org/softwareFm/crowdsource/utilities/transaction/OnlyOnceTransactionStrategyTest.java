package org.softwareFm.crowdsource.utilities.transaction;

public class OnlyOnceTransactionStrategyTest  extends AbstractITransactionStrategyTest<ITransactionStrategy>{

	@Override
	protected ITransactionStrategy makeTransactionStrategy() {
		return ITransactionStrategy.Utils.oneTryOnly();
	}

	@Override
	protected int maxTries() {
		return 1;
	}
	
	@Override
	protected String poisonedExceptionString() {
		return "Possible poisoned Transaction JobThrowingRedoTransactionException\nHave executed it 1 times. Nested exceptions [RedoTransactionException]";
	}

}
