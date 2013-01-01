package org.softwarefm.shared.usage;

import java.util.concurrent.Callable;

import org.softwarefm.shared.usage.internal.Usage;

public class UsageThread extends Thread {
	private final Usage usage;
	private final IUsageReporter reporter;
	private final Callable<Boolean> recordUsage;
	private final int period;
	private static int index;

	public UsageThread(Usage usage, IUsageReporter reporter, Callable<Boolean> recordUsage, int period) {
		this.usage = usage;
		this.reporter = reporter;
		this.recordUsage = recordUsage;
		this.period = period;
		this.setDaemon(true);
		this.setName(getClass().getSimpleName() + "/" + index++);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(period);
				if (recordUsage.call())
					reporter.report("me", usage.getStats());
				usage.nuke();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}