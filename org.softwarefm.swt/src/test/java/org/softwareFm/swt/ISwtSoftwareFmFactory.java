package org.softwareFm.swt;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.crowdsource.utilities.transaction.Transaction;
import org.softwareFm.swt.swt.Swts;

public interface ISwtSoftwareFmFactory {

	public static class Utils {

		private static AtomicInteger inSwtCount = new AtomicInteger();

		/** returns true if the calling thread is currently running inside the syncexec of the swtfunction code */
		public static boolean inSwtCallbackFunction(Display display) {
			return Thread.currentThread() == display.getThread() && inSwtCount.get() > 0;

		}

		public static ITransactionManager getSwtTransactionManager(final Display display, int workerThreads, final long timeOutMs) {
			return ITransactionManager.Utils.withFutureToTransactionFn(workerThreads, new IFunction1<Future<?>, ITransaction<?>>() {
				private final Thread swtThread = display.getThread();

				@SuppressWarnings("unchecked")
				@Override
				public ITransaction<?> apply(final Future<?> future) throws Exception {
					return new Transaction<Object>((Future<Object>) future, timeOutMs) {
						@Override
						public Object get(long timeOutMs) {
							if (Thread.currentThread() != swtThread)
								return super.get(timeOutMs);
							long startTime = System.currentTimeMillis();
							while (!future.isDone() && System.currentTimeMillis() < startTime + timeOutMs)
								Swts.dispatchUntilQueueEmpty(display);
							try {
								if (future.isDone())
									return future.get();
								else
									throw new TimeoutException();
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}

						@Override
						public String toString() {
							return "SwtDispatchingTransaction(" + future + ")";
						}
					};
				}
			}).registerCallbackExecutor(ISwtFunction1.class, new IFunction3<IServiceExecutor, IFunction1<Object, Object>, Object, Object>() {
				@Override
				public Object apply(IServiceExecutor first, final IFunction1<Object, Object> callbackFn, final Object value) throws Exception {
					final AtomicReference<Object> result = new AtomicReference<Object>();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							inSwtCount.incrementAndGet();
							try {
								result.set(Functions.call(callbackFn, value));
							} finally {
								inSwtCount.decrementAndGet();
							}
						}
					};
					display.syncExec(runnable);
					return result.get();
				}
			});
		}
	}

}
