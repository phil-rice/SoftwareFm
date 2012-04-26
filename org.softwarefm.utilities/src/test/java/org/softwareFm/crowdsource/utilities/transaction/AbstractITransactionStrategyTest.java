package org.softwareFm.crowdsource.utilities.transaction;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public abstract class AbstractITransactionStrategyTest<T extends ITransactionStrategy> extends TestCase {

	abstract protected T makeTransactionStrategy();

	abstract protected int maxTries();

	abstract protected String poisonedExceptionString();
	private T transactionStrategy;
	private IMonitor monitor;

	public void testExecutesJobNormallyIfNoRedoTransactionException() throws Exception {
		JobThrowingRedoTransactionException job = new JobThrowingRedoTransactionException("value", 0);
		assertEquals("value", transactionStrategy.execute(job, monitor));
		assertEquals(Arrays.asList(monitor), job.froms);
	}

	public void testExecutesJobSuccessfullyIfRedoTransactionExceptionThrownMaxTimes() throws Exception {
		int maxTries = maxTries();
		JobThrowingRedoTransactionException job = new JobThrowingRedoTransactionException("value", maxTries - 1);
		assertEquals("value", transactionStrategy.execute(job, monitor));
		assertEquals(Lists.times(maxTries, monitor), job.froms);
	}

	public void testThrowsPoisonedTransactionExceptionIfRedoTransactionExceptionExecutedMaxTries() {
		final JobThrowingRedoTransactionException job = new JobThrowingRedoTransactionException("value", maxTries());
		PoisonedTransactionException actualException = Tests.assertThrowsWithMessage(poisonedExceptionString(), PoisonedTransactionException.class, new Runnable() {
			@Override
			public void run() {
				try {
					transactionStrategy.execute(job, monitor);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		assertEquals(Lists.times(maxTries(), monitor), job.froms);
		assertEquals(maxTries(), actualException.getRedoTransactionExceptions().size());
		assertEquals(1, new HashSet<Thread>(job.threads).size());
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transactionStrategy = makeTransactionStrategy();
		monitor = IMonitor.Utils.noMonitor();
	}

}
