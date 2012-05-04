/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.services;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.internal.ServiceExecutor;

public interface IServiceExecutor extends IShutdown {

	public static boolean changeThreadName = false;
	
	/** The monitor is provided by the IServiceExecutor. The called task should call beginTask, worked and done. etc,Note that the task may not be finished when this service executor is finished (the task could for example end on the SWT thread). The task can be cancelled using future.cancel, or monitor.cancel */
	<T> FutureAndMonitor<T> submit(IFunction1<IMonitor, T> job);

	void addExceptionListener(IExceptionListener listener);

	void addLifeCycleListener(IServiceExecutorLifeCycleListener listener);

	public static class Utils {

		public static IServiceExecutor defaultExecutor(String pattern, int threadPoolSize) {
			return executor(pattern, threadPoolSize);
		}

		public static IServiceExecutor executor(String pattern, final int threadPoolSize, IMonitorFactory monitorFactory) {
			IServiceExecutor executor = new ServiceExecutor(pattern, monitorFactory, threadPoolSize);
			executor.addExceptionListener(IExceptionListener.Utils.syserr());
			return executor;

		}

		public static IServiceExecutor executor(String pattern, final int threadPoolSize) {
			return executor(pattern, threadPoolSize, IMonitorFactory.Utils.noMonitors);
		}
	}
}