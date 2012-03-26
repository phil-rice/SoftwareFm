package org.softwareFm.crowdsource.utilities.services;

import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public interface IMonitorFactory {

	IMonitor createMonitor();

	public static class Utils {
		public static IMonitorFactory specific(final IMonitor monitor) {
			return new IMonitorFactory() {
				@Override
				public IMonitor createMonitor() {
					return monitor;
				}
			};
		}

		public static IMonitorFactory noMonitors = new IMonitorFactory() {
			@Override
			public IMonitor createMonitor() {
				return IMonitor.Utils.noMonitor();
			}
		};
		public static IMonitorFactory sysoutMonitors = new IMonitorFactory() {
			@Override
			public IMonitor createMonitor() {
				return IMonitor.Utils.sysoutMonitor();
			}
		};
	}
}