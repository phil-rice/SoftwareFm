package org.softwarefm.server;

import java.util.concurrent.Callable;

import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.UsageThread;
import org.softwarefm.shared.usage.internal.Usage;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.runnable.Callables;

public class UsageLoadTester {

	static int port = 8095;

	public static void main(String[] args) throws InterruptedException {
		SoftwareFmServer softwareFmServer = new SoftwareFmServer(MysqlTestData.dataSource, port);
		softwareFmServer.start();
		try {
			Usage usage = new Usage(IMultipleListenerList.Utils.defaultList());
			IUsageReporter reporter = IUsageReporter.Utils.reporter("localhost", port);
			Callable<Boolean> value = Callables.value(true);
			reporter.report("someUser1", usage.getStats());
			reporter.report("someUser2", usage.getStats());
			reporter.report("someUser3", usage.getStats());
			reporter.report("someUser4", usage.getStats());
			reporter.report("someUser5", usage.getStats());
			reporter.report("someUser6", usage.getStats());
			reporter.report("someUser7", usage.getStats());
			reporter.report("someUser8", usage.getStats());
			reporter.report("someUser9", usage.getStats());
			reporter.report("someUserA", usage.getStats());
			reporter.report("someUserB", usage.getStats());
			reporter.report("someUserC", usage.getStats());

			System.out.println("Starting threads");
			new UsageThread(usage, reporter, value, 5000).start();
			Thread.sleep(10000000);
		} finally {
			System.out.println("Closing down");
			softwareFmServer.shutdown();
			System.out.println("and down");
		}
	}
}
