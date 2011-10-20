package org.softwareFm.display.swt;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.utilities.tests.IIntegrationTest;

abstract public class SwtIntegrationTest extends TestCase implements IIntegrationTest {

	protected Shell shell;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.shell = new Shell();
	}

	@Override
	protected void tearDown() throws Exception {
		dispatchUntilQueueEmpty();
		shell.dispose();
		super.tearDown();
	}

	protected void dispatchUntilQueueEmpty() {
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
	}
}
