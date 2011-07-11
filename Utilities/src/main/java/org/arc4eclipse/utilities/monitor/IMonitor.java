package org.arc4eclipse.utilities.monitor;

public interface IMonitor {

	void processed(String message, int done, int max);

	void cancel();

	boolean cancelled();

	void finish();

	boolean done();

	public static class Utils {
		public static IMonitor noMonitor() {
			return new NoMonitor();
		}

		public static IMonitor sysoutMonitor(String pattern, int quanta) {
			return new SysoutMonitor(pattern, quanta);
		}

		public static IMonitor sysoutMonitor(int quanta) {
			return new SysoutMonitor("{0} {1} of {2}", quanta);
		}

		public static IMonitor sysoutMonitor() {
			return new SysoutMonitor("{0} {1} of {2}", 1);
		}
	}

}
