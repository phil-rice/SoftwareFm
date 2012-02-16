/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.services;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.UtilityMessages;
import org.softwareFm.common.exceptions.WrappedException;

public interface IServiceExecutor {

	<T> Future<T> submit(Callable<T> callable);

	void addExceptionListener(IExceptionListener listener);

	void shutdown();

	void shutdownAndAwaitTermination(long timeout, TimeUnit unit);

	public static class Utils {

		public static IServiceExecutor defaultExecutor() {
			return executor(10);

		}

		public static IServiceExecutor executor(final int threadPoolSize) {
			IServiceExecutor executor = new IServiceExecutor() {
				private final List<IExceptionListener> listeners = Lists.newList();
				private final ExecutorService service = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000)) {
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

				@Override
				public void shutdownAndAwaitTermination(long time, TimeUnit unit) {
					try {
						service.shutdown();
						boolean suceeded = service.awaitTermination(time, unit);
						if (!suceeded){
							throw new ServerExecutorException(UtilityMessages.cannotCloseServer);
						}
					} catch (InterruptedException e) {
						throw WrappedException.wrap(e);
					}
				}
			};
			executor.addExceptionListener(IExceptionListener.Utils.syserr());
			return executor;
		}
	}
}