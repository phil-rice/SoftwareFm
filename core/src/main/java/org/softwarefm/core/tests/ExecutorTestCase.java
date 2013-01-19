package org.softwarefm.core.tests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.utilities.tests.Tests;

abstract public class ExecutorTestCase extends TestCase {

	private ThreadPoolExecutor executor;

	protected void waitUntilJobsFinished() {
		Tests.waitUntil(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return executor.getActiveCount() == 0;
			}
		});
	}

	public ThreadPoolExecutor getExecutor() {
		return executor == null ? executor = new ThreadPoolExecutor(CommonConstants.startThreadSize, CommonConstants.maxThreadSize, CommonConstants.threadStayAliveTimeMs, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(CommonConstants.maxOutStandingJobs)) : executor;
	}

	@Override
	protected void tearDown() throws Exception {
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
		super.tearDown();
	}
}
