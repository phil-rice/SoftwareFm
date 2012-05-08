/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.exceptions.CommitException;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.Functions.ConstantFunctionWithMemoryOfFroms;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction1WithExceptionHandler;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.internal.TransactionManager;

public class TransactionManagerTest extends TestCase {

	private ITransactionManager manager;
	private AtomicInteger registered1Count;
	private AtomicInteger registered2Count;

	public void testJobsAreExecuted() {
		ConstantFunctionWithMemoryOfFroms<String, String> postFunction = new ConstantFunctionWithMemoryOfFroms<String, String>("value2");
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		assertEquals("value2", manager.start(job, postFunction).get(CommonConstants.testTimeOutMs));
		assertEquals("value", Lists.getOnly(postFunction.froms));
		assertEquals(1, job.froms.size());
		assertEquals(0, registered1Count.get());
	}

	public void testIfThrowsRedoTransactionExceptionItIsRedone() {
		ConstantFunctionWithMemoryOfFroms<String, String> postFunction = new ConstantFunctionWithMemoryOfFroms<String, String>("value2");
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = new JobThrowingRedoTransactionException("value", 4); // max tries defined in setup as 5
		assertEquals("value2", manager.start(job, postFunction).get(CommonConstants.testTimeOutMs));
		assertEquals("value", Lists.getOnly(postFunction.froms));
		assertEquals(5, job.froms.size());
		assertEquals(0, registered1Count.get());
	}

	public void testThrowsPoisonedTransactionExceptionIfTooManyRedoTransactionExceptions() {
		final ConstantFunctionWithMemoryOfFroms<String, String> postFunction = new ConstantFunctionWithMemoryOfFroms<String, String>("value2");
		final ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = new JobThrowingRedoTransactionException("value", 10);
		Tests.assertThrowsWithMessage("Possible poisoned Transaction JobThrowingRedoTransactionException\nHave executed it 5 times. Nested exceptions [RedoTransactionException, RedoTransactionException, RedoTransactionException, RedoTransactionException, RedoTransactionException]", PoisonedTransactionException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(job, postFunction).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(0, postFunction.froms.size());
		assertEquals(5, job.froms.size());
		assertEquals(0, registered1Count.get());
	}

	public void testRedoTransactionInResultCallbackCausesRollback() {
		RedoTransactionException exception = new RedoTransactionException("thisone");
		final IFunction1<String, String> postFunction = Functions.expectionIfCalled(exception);
		final ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		Tests.assertThrowsWithMessage("thisone", RedoTransactionException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(job, postFunction).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(0, registered1Count.get());
	}

	public void testGetMyTransactionStrategy() {
		IFunction1<String, String> postFunction = new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				assertNotNull(manager.myTransactionStrategy());
				return from;
			}
		};
		IFunction1<IMonitor, String> job = new IFunction1<IMonitor, String>() {
			@Override
			public String apply(IMonitor from) throws Exception {
				assertNotNull(manager.myTransactionStrategy());
				return "value";
			}
		};
		manager.start(job, postFunction).get(CommonConstants.testTimeOutMs);
	}

	public void testGetMyTransactionStrategyIfNotInTransactionCausesException() {
		Tests.assertThrowsWithMessage(null, NotInTransactionException.class, new Runnable() {
			@Override
			public void run() {
				manager.myTransactionStrategy();
			}
		});
	}

	public void testJobsAreExecutedOnADifferentThread() {
		ConstantFunctionWithMemoryOfFroms<String, String> postFunction = new ConstantFunctionWithMemoryOfFroms<String, String>("value2");
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, postFunction).get(CommonConstants.testTimeOutMs);
		Thread postFunctionThread = Lists.getOnly(postFunction.threads);
		assertTrue(Thread.currentThread() != postFunctionThread);
		assertEquals(Lists.getOnly(job.threads), postFunctionThread);
		assertEquals(0, registered1Count.get());
	}

	public void testActiveJobs() throws InterruptedException {
		ConstantFnWithKick<IMonitor, String> fn1 = new ConstantFnWithKick<IMonitor, String>("value1");
		ConstantFnWithKick<IMonitor, String> fn2 = new ConstantFnWithKick<IMonitor, String>("value2");
		ConstantFnWithKick<IMonitor, String> fn3 = new ConstantFnWithKick<IMonitor, String>("value3");
		ConstantFnWithKick<IMonitor, String> fn4 = new ConstantFnWithKick<IMonitor, String>("value4");

		ConstantFnWithKick<String, String> cb1 = new ConstantFnWithKick<String, String>("res1");
		ConstantFnWithKick<String, String> cb2 = new ConstantFnWithKick<String, String>("res2");
		ConstantFnWithKick<String, String> cb3 = new ConstantFnWithKick<String, String>("res3");
		ConstantFnWithKick<String, String> cb4 = new ConstantFnWithKick<String, String>("res4");

		assertEquals(0, manager.activeJobs());
		manager.start(fn1, cb1);
		manager.start(fn2, cb2);
		manager.start(fn3, cb3);
		manager.start(fn4, cb4);

		Tests.waitUntil(activeJobsAre(manager, 4));
		fn1.kick();
		fn2.kick();
		Thread.sleep(10);// giving it time to go wrong
		Tests.waitUntil(activeJobsAre(manager, 4));
		cb1.kick();
		Tests.waitUntil(activeJobsAre(manager, 3));
		cb2.kick();
		Tests.waitUntil(activeJobsAre(manager, 2));
		fn3.kick();
		fn4.kick();
		Thread.sleep(10);// giving it time to go wrong
		Tests.waitUntil(activeJobsAre(manager, 2));
		cb3.kick();
		cb4.kick();
		Tests.waitUntil(activeJobsAre(manager, 0));
	}

	public static Callable<Boolean> activeJobsAre(final ITransactionManager manager, final int value) {
		return new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return manager.activeJobs() == value;
			}
		};
	}

	public void testNestedTransactionsExecutedOnSameThread() {
		ConstantFunctionWithMemoryOfFroms<String, String> postFunction1 = new ConstantFunctionWithMemoryOfFroms<String, String>("value1-b");
		final ConstantFunctionWithMemoryOfFroms<String, String> postFunction2 = new ConstantFunctionWithMemoryOfFroms<String, String>("value2-b");
		final AtomicReference<Thread> thread1 = new AtomicReference<Thread>();
		final AtomicReference<Thread> thread2 = new AtomicReference<Thread>();
		final AtomicReference<ITransaction<String>> nestedTransaction = new AtomicReference<ITransaction<String>>();
		String result = manager.start(new IFunction1<IMonitor, String>() {
			@Override
			public String apply(IMonitor from) throws Exception {
				from.beginTask("asd", 1);
				thread1.set(Thread.currentThread());
				ITransaction<String> nested = manager.start(new IFunction1<IMonitor, String>() {
					@Override
					public String apply(IMonitor from) throws Exception {
						thread2.set(Thread.currentThread());
						from.beginTask("asd", 1);
						return "nestedResult";
					}
				}, postFunction2);
				String result = nested.get(CommonConstants.testTimeOutMs);
				assertTrue(nested.isDone());
				nestedTransaction.set(nested);
				return result;
			}
		}, postFunction1).get(CommonConstants.testTimeOutMs);

		assertTrue(thread1.get() != Thread.currentThread());
		assertSame(thread1.get(), thread2.get());

		assertEquals("value1-b", result);
		assertEquals("value2-b", Lists.getOnly(postFunction1.froms));
		assertEquals("nestedResult", Lists.getOnly(postFunction2.froms));
	}

	public void testNestedTransactionalsAreIncludedInCommits() {
		final ITransactional tra = EasyMock.createMock(ITransactional.class);
		final ITransactional trb = EasyMock.createMock(ITransactional.class);
		final ITransactional tr1a = EasyMock.createMock(ITransactional.class);
		final ITransactional tr1b = EasyMock.createMock(ITransactional.class);
		final ITransactional tr2a = EasyMock.createMock(ITransactional.class);
		final ITransactional tr2b = EasyMock.createMock(ITransactional.class);
		tra.commit();
		trb.commit();
		tr1a.commit();
		tr1b.commit();
		tr2a.commit();
		tr2b.commit();
		EasyMock.replay(tra, trb, tr1a, tr1b, tr2a, tr2b);

		final ConstantFunctionWithMemoryOfFroms<IMonitor, String> nestedJob1 = Functions.<String> constantWithMemoryOfMonitor("value1");
		final ConstantFunctionWithMemoryOfFroms<IMonitor, String> nestedJob2 = Functions.<String> constantWithMemoryOfMonitor("value2");

		final IFunction1<String, String> identity = Functions.<String, String> identity();
		manager.start(new IFunction1<IMonitor, String>() {
			@Override
			public String apply(IMonitor from) throws Exception {
				return manager.start(nestedJob1, identity, tr1a, tr1b).get(CommonConstants.testTimeOutMs) + manager.start(nestedJob2, identity, tr2a, tr2b).get(CommonConstants.testTimeOutMs);
			}
		}, identity, tra, trb).get(CommonConstants.testTimeOutMs);

		EasyMock.verify(tra, trb, tr1a, tr1b, tr2a, tr2b);
	}

	public void testCallbackIsExecutedByRegisteredExecutorIfMarkerInterfacePresent() {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, new IFunctionWithMarker1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return null;
			}
		}).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(0, registered2Count.get());

		manager.start(job, new IFunctionWithMarker2<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return null;
			}
		}).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(1, registered2Count.get());
	}

	public void testIfHasTwoMarkersUsesFirstRegistered() {
		ConstantFunctionWithMemoryOfFroms<IMonitor, String> job = Functions.<String> constantWithMemoryOfMonitor("value");
		manager.start(job, new FunctionWithTwoMarkers<String, String>()).get(CommonConstants.testTimeOutMs);
		assertEquals(1, registered1Count.get());
		assertEquals(0, registered2Count.get());
	}

	public void testCallbackNotCalledIfExceptionOccursInFunction() {
		final IllegalStateException expected = new IllegalStateException();
		RuntimeException actual = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				try {
					manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), Functions.<Object, Object> expectionIfCalled()).get(CommonConstants.testTimeOutMs);
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
					manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), new IFunction1WithExceptionHandler<Object, Object>() {
						@Override
						public Object apply(Object t) throws Exception {
							fail();
							return null;
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

		assertEquals("value", manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), Functions.identity(), mock1, mock2, "notTransactional", mock3).get(CommonConstants.testTimeOutMs));

		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testAllCommitsAreCalledThenRollbacksAreCalledIfCommitExceptions_withOneExceptionThrowsIt() {
		RuntimeException expected = new RuntimeException();

		final ITransactional mock1 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock2 = EasyMock.createMock(ITransactional.class);
		final ITransactional mock3 = EasyMock.createMock(ITransactional.class);
		mock1.commit();
		mock2.commit();
		EasyMock.expectLastCall().andThrow(expected);
		mock3.commit();
		
		mock1.rollback();
		mock2.rollback();
		mock3.rollback();
		
		EasyMock.replay(mock1, mock2, mock3);

		CommitException actual = Tests.assertThrows(CommitException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), Functions.identity(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(expected, Lists.getOnly(actual.getExceptions()));
		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testAllCommitsAreCalledThenRollbacksAreCalledIfCommitExceptions_withMultipleExceptionThrowsIt() {
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

		mock1.rollback();
		mock2.rollback();
		mock3.rollback();

		EasyMock.replay(mock1, mock2, mock3);

		AggregateException actual = Tests.assertThrows(AggregateException.class, new Runnable() {
			@Override
			public void run() {
				manager.start(Functions.<Object> constantWithMemoryOfMonitor("value"), Functions.identity(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
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
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(expected), Functions.identity(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
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
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(original), Functions.identity(), mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(Arrays.asList(original, expected1, expected2), actual.getExceptions());
		assertEquals("class java.lang.RuntimeException/orig,class java.lang.RuntimeException/one,class java.lang.RuntimeException/null", actual.getMessage());

		EasyMock.verify(mock1, mock2, mock3);
	}

	public void testExceptionInHandlerDoesntStopRollbackAndCausesAggregateException() {
		final Exception original = new RuntimeException("orig");
		final RuntimeException callbackException = new RuntimeException("callback");
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
				manager.start(Functions.<IMonitor, Object> expectionIfCalled(original), new IFunction1WithExceptionHandler<Object, Object>() {
					@Override
					public void handle(Exception e) {
						throw callbackException;
					}

					@Override
					public Object apply(Object from) throws Exception {
						fail();
						return null;
					}
				}, mock1, mock2, mock3).get(CommonConstants.testTimeOutMs);
			}
		});
		assertEquals(Arrays.asList(original, callbackException, expected1, expected2), actual.getExceptions());
		assertEquals("class java.lang.RuntimeException/orig,class java.lang.RuntimeException/callback,class java.lang.RuntimeException/one,class java.lang.RuntimeException/null", actual.getMessage());

		EasyMock.verify(mock1, mock2, mock3);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IServiceExecutor defaultExecutor = IServiceExecutor.Utils.defaultExecutor(getClass().getSimpleName() + "-{0}", 10);
		registered1Count = new AtomicInteger();
		registered2Count = new AtomicInteger();
		manager = new TransactionManager(defaultExecutor, new TransactionManager.DefaultFutureToTransactionDn(CommonConstants.testTimeOutMs), ITransactionStrategy.Utils.backOffAndRetry(1, 5)).//
				registerCallbackExecutor(IFunctionWithMarker1.class, new IFunction3<IServiceExecutor, IFunction1<Object, Object>, Object, Object>() {

					@Override
					public Object apply(IServiceExecutor first, IFunction1<Object, Object> from2, Object from3) throws Exception {
						assertEquals(defaultExecutor, first);
						registered1Count.getAndIncrement();
						return null;
					}
				}).//
				registerCallbackExecutor(IFunctionWithMarker2.class, new IFunction3<IServiceExecutor, IFunction1<Object, Object>, Object, Object>() {
					@Override
					public Object apply(IServiceExecutor from1, IFunction1<Object, Object> from2, Object from3) throws Exception {
						assertEquals(defaultExecutor, from1);
						registered2Count.getAndIncrement();
						return null;
					}
				});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null)
			manager.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	static interface IFunctionWithMarker1<From, To> extends IFunction1<From, To> {

	}

	static interface IFunctionWithMarker2<From, To> extends IFunction1<From, To> {

	}

	static class FunctionWithTwoMarkers<From, To> implements IFunctionWithMarker1<From, To>, IFunctionWithMarker2<From, To> {
		@Override
		public To apply(From from) throws Exception {
			Assert.fail("Shouldn't be called");
			return null;
		}
	}
}