package org.softwarefm.loadtest;

import org.softwareFm.server.IUsage;

public class TimeJustUsage {
	public static void main(String[] args) {
		IUsage usage = IUsage.Utils.defaultUsage();
		long startTime = System.currentTimeMillis();
		usage.start();
		for (int i = 0; i < 1000; i++)
			usage.monitor("test", "value", i);
		System.out.println("took: " + (System.currentTimeMillis() - startTime) / 1000);

	}
}
