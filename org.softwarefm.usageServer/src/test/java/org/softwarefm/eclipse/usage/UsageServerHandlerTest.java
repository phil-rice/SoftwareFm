package org.softwarefm.eclipse.usage;

import junit.framework.TestCase;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageReporter;
import org.softwarefm.eclipse.usage.IUsageServer;
import org.softwarefm.eclipse.usage.internal.Usage;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.http.IHttpServer;

public class UsageServerHandlerTest extends TestCase {

	private final int port = 8190;
	private IHttpServer usageServer;
	private MemoryCallback<IUsage> memoryCallback;
	private MemoryCallback<Throwable> memoryThrowable;

	public void testShutsDown() {
		usageServer.start();
	}
	
	public void testUsageIsDecodedAndSentToCallback(){
		usageServer.start();
		IUsageReporter reporter = IUsageReporter.Utils.reporter("localhost", port);
		Usage usage = new Usage(IMultipleListenerList.Utils.defaultList());
		usage.selected("a");
		usage.selected("a");
		usage.selected("b");
		
		reporter.report(usage);
		assertEquals(usage.getStats(), memoryCallback.getOnlyResult().getStats());
	
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memoryCallback = new MemoryCallback<IUsage>();
		memoryThrowable = new MemoryCallback<Throwable>();
		usageServer = IUsageServer.Utils.usageServer(port, memoryCallback, memoryThrowable);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		memoryThrowable.assertNotCalled();
		if (usageServer != null)
			usageServer.shutdown();
	}
}
