package org.softwarefm.server.usage;

import org.softwarefm.eclipse.usage.IUsageReporter;
import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.server.AbstractServerHandlerTest;
import org.softwarefm.server.usage.internal.UsageServerHandler;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.tests.Tests;

public class UsageServerHandlerTest extends AbstractServerHandlerTest<UsageServerHandler> {

	private MemoryCallback<ISimpleMap<String, UsageStatData>> memoryCallback;

	public void testShutsDown() {
		httpServer.start();
	}

	public void testUsageIsDecodedAndSentToCallback() {
		httpServer.start();
		IUsageReporter reporter = IUsageReporter.Utils.reporter("localhost", port);

		reporter.report(UsageTestData.statsa1b3);
		Tests.assertEquals(memoryCallback.getOnlyResult(),UsageTestData.statsa1b3);

	}

	@Override
	protected UsageServerHandler createHandler() {
		return new UsageServerHandler(memoryCallback = new MemoryCallback<ISimpleMap<String, UsageStatData>>());
	}

}
