package org.softwareFm.crowdsource.utilities.services;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public interface IServiceExecutorLifeCycleListener {

	void starting(IFunction1<IMonitor,?> task);

	<T> void finished(IFunction1<IMonitor,T> task, T value);

	void exception(IFunction1<IMonitor,?> task, Throwable throwable);

	public static class Utils {
		public static IServiceExecutorLifeCycleListener sysoutExecutorLifeCycleListener() {
			return new IServiceExecutorLifeCycleListener() {

				@Override
				public void starting(IFunction1<IMonitor, ?> task) {
					System.out.println("Starting: " + task);
				}

				@Override
				public <T> void finished(IFunction1<IMonitor, T> task, T value) {
					System.out.println("Finished: " + task +" Value: " + value);
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
