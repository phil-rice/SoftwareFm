package org.softwareFm.httpClient.api;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.softwareFm.utilities.collections.Lists;

public interface IServiceExecutor {

	<T> Future<T> submit(Callable<T> callable);

	void addExceptionListener(IExceptionListener listener);

	void shutdown();

	public static class Utils {
		public static IServiceExecutor defaultExecutor() {
			IServiceExecutor executor = new IServiceExecutor() {
				private final List<IExceptionListener> listeners = Lists.newList();
				private final ExecutorService service = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)) {
					@Override
					protected void afterExecute(Runnable r, Throwable t) {
						super.afterExecute(r, t);

					}
				};

				@Override
				public <T> Future<T> submit(final Callable<T> callable) {
					return service.submit(new Callable<T>() {
						@Override
						public T call() throws Exception {
							try {
								return callable.call();
							} catch (Exception e) {
								if (e != null)
									for (IExceptionListener listener : listeners)
										listener.exceptionOccured(e);
								throw e;
							}
						}
					});
				}

				@Override
				public void addExceptionListener(IExceptionListener listener) {
					listeners.add(listener);

				}

				@Override
				public void shutdown() {
					service.shutdown();
				}
			};
			executor.addExceptionListener(IExceptionListener.Utils.syserr());
			return executor;
		}
	}
}
