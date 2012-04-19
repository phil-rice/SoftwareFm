/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.services;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public interface IServiceExecutorLifeCycleListener {

	void starting(IFunction1<IMonitor, ?> task);

	<T> void finished(IFunction1<IMonitor, T> task, T value);

	void exception(IFunction1<IMonitor, ?> task, Throwable throwable);

	public static class Utils {
		public static IServiceExecutorLifeCycleListener sysoutExecutorLifeCycleListener() {
			return new IServiceExecutorLifeCycleListener() {

				@Override
				public void starting(IFunction1<IMonitor, ?> task) {
					System.out.println("Starting: " + task);
				}

				@Override
				public <T> void finished(IFunction1<IMonitor, T> task, T value) {
					System.out.println("Finished: " + task + " Value: " + value);
				}

				@Override
				public void exception(IFunction1<IMonitor, ?> task, Throwable throwable) {
					System.out.println("exception: " + task + ", " + throwable);
					throwable.printStackTrace(System.out);
				}
			};
		}
	}
}