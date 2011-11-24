package org.softwareFm.display.swt;

import org.softwareFm.utilities.services.IServiceExecutor;

abstract public class SwtIntegrationAndServiceTest extends SwtIntegrationTest {

	protected IServiceExecutor service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = IServiceExecutor.Utils.defaultExecutor();
	}

	@Override
	protected void tearDown() throws Exception {
		service.shutdown();
		super.tearDown();
	}

}
