package org.softwareFm.swt;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
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

		public static ITransactionManager getSwtTransactionManager(final Display display, final long timeOutMs) {
			return ITransactionManager.Utils.withFutureToTransactionFn(new IFunction1<Future<?>, ITransaction<?>>() {
				private final Thread swtThread = display.getThread();
				@SuppressWarnings("unchecked")
				@Override
				public ITransaction<?> apply(final Future<?> future) throws Exception {
					return new Transaction<Object>((Future<Object>) future, timeOutMs) { 
						@Override
						public Object get() {
							if (Thread.currentThread() != swtThread)
								return super.get();
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
					};
				}
			}).registerCallbackExecutor(ISwtFunction1.class, new IFunction3<IServiceExecutor, IFunction1<Object, Object>, Object, Object>() {
				@Override
				public Object apply(IServiceExecutor first, final IFunction1<Object, Object> callbackFn, final Object value) throws Exception {
					final AtomicReference<Object> result = new AtomicReference<Object>();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							result.set(Functions.call(callbackFn, value));
						}
					};
					if (Thread.currentThread() == display.getThread())
						runnable.run();
					else
						display.syncExec(runnable);
					return result.get();
				}
			});
		}
	}
	
}
