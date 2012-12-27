package org.softwarefm.server.usage;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.IUsageReporter;
import org.softwarefm.server.AbstractServerHandlerTest;
import org.softwarefm.server.usage.internal.UsageServerHandler;
import org.softwarefm.shared.usage.UsageTestData;

public class UsageServerHandlerTest extends AbstractServerHandlerTest<UsageServerHandler> {

	private IUsageCallback memoryCallback;

	public void testShutsDown() {
		EasyMock.replay(memoryCallback);
		httpServer.start();
	}

	public void testUsageIsDecodedAndSentToCallback() {
		memoryCallback.process("","someUser", UsageTestData.statsa1b3);
		EasyMock.replay(memoryCallback);
		httpServer.start();
		IUsageReporter reporter = IUsageReporter.Utils.reporter("localhost", port);

		reporter.report("someUser", UsageTestData.statsa1b3);

	}

	@Override
	protected UsageServerHandler createHandler() {
		return new UsageServerHandler(IUsagePersistance.Utils.persistance(), memoryCallback =EasyMock.createMock(IUsageCallback.class));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(memoryCallback);
	}

}
