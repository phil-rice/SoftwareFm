package org.softwareFm.crowdsource.utilities.transaction;

public class BackOffAndRedoTransactionStrategyTest extends AbstractITransactionStrategyTest<ITransactionStrategy> {

	@Override
	protected ITransactionStrategy makeTransactionStrategy() {
		return ITransactionStrategy.Utils.backOffAndRetry(5, maxTries());
	}

	public void testActuallyBacksOff() throws Exception {
		long startTime = System.nanoTime();
		super.testExecutesJobSuccessfullyIfRedoTransactionExceptionThrownMaxTimes();
		long endTime = System.nanoTime();
		long milliseconds = (endTime - startTime) / 1000000;
		assertTrue("Time: " + Long.toString(milliseconds) + " times: " + maxTries(), milliseconds >= (maxTries() - 1) * 5 - 2); // the -2 is to deal with rounding errors and stuff
	}

	@Override
	protected int maxTries() {
		return 5;
	}

	@Override
	protected String poisonedExceptionString() {
		return "Possible poisoned Transaction JobThrowingRedoTransactionException\nHave executed it 5 times. Nested exceptions [RedoTransactionException, RedoTransactionException, RedoTransactionException, RedoTransactionException, RedoTransactionException]";
	}

}
