/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.services;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.internal.ServiceExecutor;

public interface IServiceExecutor {

	/** The monitor is provided by the IServiceExecutor. The called task should call beginTask, worked and done. etc,Note that the task may not be finished when this service executor is finished (the task could for example end on the SWT thread). The task can be cancelled using future.cancel, or monitor.cancel */
	<T> Future<T> submit(IFunction1<IMonitor, T> job);

	void addExceptionListener(IExceptionListener listener);

	void addLifeCycleListener(IServiceExecutorLifeCycleListener listener);

	void shutdown();

	void shutdownAndAwaitTermination(long timeout, TimeUnit unit);

	public static class Utils {

		public static IServiceExecutor defaultExecutor() {
			return executor(10);
		}

		public static IServiceExecutor executor(final int threadPoolSize, IMonitorFactory monitorFactory) {
			IServiceExecutor executor = new ServiceExecutor(monitorFactory, threadPoolSize);
			executor.addExceptionListener(IExceptionListener.Utils.syserr());
			return executor;
			
		}
		public static IServiceExecutor executor(final int threadPoolSize) {
			return executor(threadPoolSize, IMonitorFactory.Utils.noMonitors);
		}
	}
}