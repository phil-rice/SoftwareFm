package org.softwarefm.shared.usage;

import org.softwarefm.shared.usage.internal.Usage;

public class UsageThread extends Thread {
	private final Usage usage;
	private final IUsageReporter reporter;
	private final int period;
	private static int index;
	private final IUsageThreadData threadData;

	public UsageThread(Usage usage, IUsageReporter reporter, IUsageThreadData threadData, int period) {
		this.usage = usage;
		this.reporter = reporter;
		this.threadData = threadData;
		this.period = period;
		this.setDaemon(true);
		this.setName(getClass().getSimpleName() + "/" + index++);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(period);
				report();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void report() throws Exception {
		String myName = threadData.myName();
		if (myName != null && threadData.recordUsage()) {
			reporter.report(myName, usage.getStats());
			usage.nuke();
		}
	}

}