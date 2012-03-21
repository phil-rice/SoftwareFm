package org.softwareFm.common.services.internal;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.Functions.ConstantFunctionWithMemoryOfFroms;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.crowdsource.utilities.runnable.Runnables.CountRunnable;
import org.softwareFm.crowdsource.utilities.services.IMonitorFactory;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutorLifeCycleListener;
import org.softwareFm.crowdsource.utilities.services.internal.ServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.internal.ServiceExecutor.TrackBeginMonitor;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class ServiceExecutorTest extends TestCase {

	private IServiceExecutor serviceExecutor;
	private final int noOfThreads = 5;

	public void testSubmittedTasksGetCalled() throws Exception {
		CountRunnable count = Runnables.count();
		checkCanRunInParallel(count, 0);
		assertEquals(noOfThreads, count.getCount());
	}

	public void testIfExceptionIsThrownNewThreadIsCreatedAndCanStillExecuteInParallel() throws Exception {
		checkCanRunInParallel(Runnables.noRunnable, 0);
		checkCanRunInParallel(Runnables.exception(), 5);
		checkCanRunInParallel(Runnables.noRunnable, 0);
		checkCanRunInParallel(Runnables.exception(), 5);
		checkCanRunInParallel(Runnables.noRunnable, 0);
	}

	public void testPassesMonitorFromMonitorFactoryToJob() throws Exception {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = createSimpleJob("some result");
		IMonitor monitor = IMonitor.Utils.noMonitor();
		executeWithSpecificMonitor(job, monitor);
		ServiceExecutor.TrackBeginMonitor actual = (TrackBeginMonitor) Lists.getOnly(job.froms);
		assertEquals(monitor, actual.getDelegate());
	}

	public void testThrowsExceptionIfBeginWasntCalled() {
		final IFunction1<IMonitor, String> job = Functions.<IMonitor, String> constant("result");
		WrappedException e = Tests.assertThrows(WrappedException.class, new Runnable() {
			@Override
			public void run() {
				try {
					serviceExecutor.submit(job).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		Throwable ExecutionException = e.unwrap();
		IllegalStateException realException = (IllegalStateException) ExecutionException.getCause();
		assertEquals("Monitor begin method was not called for Constant: result", realException.getMessage());
	}

	public void testListenerCalled() throws Exception {
		String result = "result";
		IFunction1<IMonitor, String> job = createSimpleJob(result);

		IServiceExecutorLifeCycleListener listener1 = EasyMock.createStrictMock(IServiceExecutorLifeCycleListener.class);
		listener1.starting(job);
		listener1.finished(job, result);
		EasyMock.replay(listener1);

		serviceExecutor.addLifeCycleListener(listener1);
		serviceExecutor.submit(job).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		EasyMock.verify(listener1);
	}

	public void testListenerCalledWithException() throws Exception {
		RuntimeException runtimeException = new RuntimeException();
		final IFunction1<IMonitor, String> job = Functions.exceptionIfCalled(runtimeException);

		IServiceExecutorLifeCycleListener listener1 = EasyMock.createStrictMock(IServiceExecutorLifeCycleListener.class);
		listener1.starting(job);
		listener1.exception(job, runtimeException);
		EasyMock.replay(listener1);

		serviceExecutor.addLifeCycleListener(listener1);
		RuntimeException e = Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				try {
					serviceExecutor.submit(job).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
				} catch (ExecutionException e) {
					throw WrappedException.wrap(e.getCause());
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		assertEquals(runtimeException, e);
		EasyMock.verify(listener1);
	}

	private void checkCanRunInParallel(final Runnable task, int expectedExceptionCount) throws Exception {
		final CountDownLatch waitLatch = new CountDownLatch(1);
		final CountDownLatch finishLatch = new CountDownLatch(noOfThreads);

		List<Future<Integer>> futures = Lists.newList();
		final AtomicInteger name = new AtomicInteger();
		for (int i = 0; i < noOfThreads; i++) {
			futures.add(serviceExecutor.submit(new IFunction1<IMonitor, Integer>() {
				@Override
				public Integer apply(IMonitor from) throws Exception {
					try {
						from.beginTask("test", IMonitor.UNKNOWN);
						try {
							waitLatch.await();
							int result = name.getAndIncrement();
							task.run();
							return result;
						} finally {
							from.done();
						}
					} finally {
						finishLatch.countDown();
					}
				}

			}));
		}
		for (Future<Integer> future : futures) {
			assertFalse(future.isCancelled());
			assertFalse(future.isDone());
		}
		waitLatch.countDown();
		finishLatch.await(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		Set<Integer> results = Sets.newSet();
		int exceptionCount = 0;
		int i = 0;
		Set<Integer> expected = Sets.newSet();
		for (Future<Integer> future : futures) {
			try {
				Integer result = future.get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
				results.add(result);
				expected.add(i);
			} catch (Exception e) {
				exceptionCount++;
			}
			i++;
			assertFalse(future.isCancelled());
			assertTrue(future.isDone());
		}
		assertEquals(exceptionCount, exceptionCount);
		assertEquals(expected, results);
	}

	private ConstantFunctionWithMemoryOfFroms<IMonitor, String> createSimpleJob(String result) {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = new ConstantFunctionWithMemoryOfFroms<IMonitor, String>(result) {
			@Override
			public String apply(IMonitor from) throws Exception {
				from.beginTask("somename", 1);
				return super.apply(from);
			}
		};
		return job;
	}

	private void executeWithSpecificMonitor(IFunction1<IMonitor, String> job, IMonitor monitor) throws InterruptedException, ExecutionException, TimeoutException {
		IServiceExecutor executorWithMockFactory = IServiceExecutor.Utils.executor(10, IMonitorFactory.Utils.specific(monitor));
		try {
			executorWithMockFactory.submit(job).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		} finally {
			executorWithMockFactory.shutdown();
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serviceExecutor = IServiceExecutor.Utils.executor(noOfThreads);
	}

	@Override
	protected void tearDown() throws Exception {
		if (serviceExecutor != null)
			serviceExecutor.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		super.tearDown();
	}
}
