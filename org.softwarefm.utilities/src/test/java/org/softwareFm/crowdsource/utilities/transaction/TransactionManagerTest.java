package org.softwareFm.crowdsource.utilities.transaction;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.callbacks.ICallbackWithExceptionHandler;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryWithThreadCallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.Functions.ConstantFunctionWithMemoryOfFroms;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.internal.TransactionManager;

public class TransactionManagerTest extends TestCase {

	private ITransactionManager manager;
	private AtomicInteger registered1Count;
	private AtomicInteger registered2Count;

	public void testJobsAreExecuted() {
		MemoryWithThreadCallback<String> callback = ICallback.Utils.<String> memoryWithThread();
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		assertEquals("value", manager.start(job, callback).get(CommonConstants.testTimeOutMs));
		assertEquals("value", callback.getOnlyResult());
		assertEquals(1, job.froms.size());
		assertEquals(0, registered1Count.get());
	}

	public void testJobsAreExecutedOnADifferentThread() {
		MemoryWithThreadCallback<String> callback = ICallback.Utils.<String> memoryWithThread();
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, callback).get(CommonConstants.testTimeOutMs);
		assertTrue(Thread.currentThread() != callback.getOnlyThread());
		assertEquals(Lists.getOnly(job.threads), callback.getOnlyThread());
		assertEquals(0, registered1Count.get());
	}

	public void testCallbackIsExecutedByRegisteredExecutorIfMarkerInterfacePresent() {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, new ICallbackWithMarker1<String>() {
			@Override
			public void process(String t) throws Exception {
			}
		}).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(0, registered2Count.get());

		manager.start(job, new ICallbackWithMarker2<String>() {
			@Override
			public void process(String t) throws Exception {
			}
		}).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(1, registered2Count.get());
	}

	public void testIfHasTwoMarkersUsesFirstRegistered() {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, new CallbackWithTwoMarkers<String>()).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(0, registered2Count.get());
	}

	public void testCallbackNotCalledIfExceptionOccursInFunction() {
		final IllegalStateException expected = new IllegalStateException();
		RuntimeException actual = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				try {
					manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), ICallback.Utils.exception("")).get(CommonConstants.testTimeOutMs);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		assertEquals(actual.toString(), expected, actual);
	}

	public void testCallbackCalledIfResultCallbackHasExceptionHandler() {
		final IllegalStateException expected = new IllegalStateException();
		final AtomicReference<Exception> handlerException = new AtomicReference<Exception>();
		RuntimeException actual = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				try {
					manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), new ICallbackWithExceptionHandler<Object>() {
						@Override
						public void process(Object t) throws Exception {
							fail();
						}

						@Override
						public void handle(Exception e) {
							assertEquals(null, handlerException.get());
							handlerException.set(e);
						}
					}).get(CommonConstants.testTimeOutMs);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		assertEquals(actual.toString(), expected, actual);
		assertEquals(expected, handlerException.get());
	}

	public void testCommitsAreCalledIfSuccessfulExecution() {
		ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.commit();
		mock2.commit();
		mock3.commit();
		EasyMock.replay(mock1, mock2, mock3);

		assertEquals("value", manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), ICallback.Utils.noCallback(), mock1, mock2, "notTransactional", mock3).get(CommonConstants.testTimeOutMs));

		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testCommitsAreCalledEvenIfEarlierCommitFails_withOneExceptionThrowsIt() {
		RuntimeException expected = new RuntimeException();

		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.commit();
		mock2.commit();
		EasyMock.expectLastCall().andThrow(expected);
		mock3.commit();
		EasyMock.replay(mock1, mock2, mock3);

		RuntimeException actual = Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), ICallback.Utils.noCallback(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(expected, actual);
		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testCommitsAreCalledEvenIfEarlierCommitFails_withMultipleExceptionThrowsAggregate() {
		RuntimeException expected1 = new RuntimeException("mess1");
		RuntimeException expected2 = new RuntimeException();

		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.commit();
		EasyMock.expectLastCall().andThrow(expected1);
		mock2.commit();
		EasyMock.expectLastCall().andThrow(expected2);
		mock3.commit();
		EasyMock.replay(mock1, mock2, mock3);

		AggregateException actual = Tests.assertThrows(AggregateException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), ICallback.Utils.noCallback(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(Arrays.asList(expected1, expected2), actual.getExceptions());
		assertEquals("class java.lang.RuntimeException/mess1,class java.lang.RuntimeException/null", actual.getMessage());
		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testRollbacksAreCalledIfFunctionThrowsException() {
		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.rollback();
		mock2.rollback();
		mock3.rollback();
		EasyMock.replay(mock1, mock2, mock3);

		final Exception expected = new RuntimeException();
		assertEquals(expected, Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), ICallback.Utils.noCallback(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		}));

		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testExceptionInRollbackCausesAggregateExceptionButAllRollbacksCalled() {
		final Exception original = new RuntimeException("orig");
		final Exception expected1 = new RuntimeException("one");
		final Exception expected2 = new RuntimeException();

		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.rollback();
		EasyMock.expectLastCall().andThrow(expected1);
		mock2.rollback();
		EasyMock.expectLastCall().andThrow(expected2);
		mock3.rollback();
		EasyMock.replay(mock1, mock2, mock3);

		AggregateException actual = Tests.assertThrows(AggregateException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(original), ICallback.Utils.noCallback(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(Arrays.asList(original, expected1, expected2), actual.getExceptions());
		assertEquals("class java.lang.RuntimeException/orig,class java.lang.RuntimeException/one,class java.lang.RuntimeException/null", actual.getMessage());

		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testExceptionInHandlerDoesntStopRollbackAndCausesAggregateException() {
		final Exception original = new RuntimeException("orig");
		final RuntimeException callback = new RuntimeException("callback");
		final Exception expected1 = new RuntimeException("one");
		final Exception expected2 = new RuntimeException();

		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.rollback();
		EasyMock.expectLastCall().andThrow(expected1);
		mock2.rollback();
		EasyMock.expectLastCall().andThrow(expected2);
		mock3.rollback();
		EasyMock.replay(mock1, mock2, mock3);

		AggregateException actual = Tests.assertThrows(AggregateException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(original), new ICallbackWithExceptionHandler<Object>() {
					@Override
					public void process(Object t) throws Exception {
						fail();
					}

					@Override
					public void handle(Exception e) {
						throw callback;
					}
				}, mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(Arrays.asList(original, callback, expected1, expected2), actual.getExceptions());
		assertEquals("class java.lang.RuntimeException/orig,class java.lang.RuntimeException/callback,class java.lang.RuntimeException/one,class java.lang.RuntimeException/null", actual.getMessage());

		EasyMock.verify(mock1, mock2, mock3);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IServiceExecutor defaultExecutor = IServiceExecutor.Utils.defaultExecutor();
		registered1Count = new AtomicInteger();
		registered2Count = new AtomicInteger();
		manager = new TransactionManager(defaultExecutor).//
				registerCallbackExecutor(ICallbackWithMarker1.class, new ICallback3<IServiceExecutor, ICallback<?>, Object>() {
					@Override
					public void process(IServiceExecutor first, ICallback<?> second, Object third) throws Exception {
						assertEquals(defaultExecutor, first);
						registered1Count.getAndIncrement();
					}
				}).//
				registerCallbackExecutor(ICallbackWithMarker2.class, new ICallback3<IServiceExecutor, ICallback<?>, Object>() {
					@Override
					public void process(IServiceExecutor first, ICallback<?> second, Object third) throws Exception {
						assertEquals(defaultExecutor, first);
						registered2Count.getAndIncrement();
					}
				});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null)
			manager.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	static interface ICallbackWithMarker1<T> extends ICallback<T> {

	}

	static interface ICallbackWithMarker2<T> extends ICallback<T> {

	}

	static class CallbackWithTwoMarkers<T> implements ICallbackWithMarker1<T>, ICallbackWithMarker2<T> {
		@Override
		public void process(T t) throws Exception {
			fail();
		}
	}
}
