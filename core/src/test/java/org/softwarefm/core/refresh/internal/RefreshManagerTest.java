package org.softwarefm.core.refresh.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.core.refresh.IRefresh;
import org.softwarefm.core.refresh.IRefreshManager;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class RefreshManagerTest extends TestCase {

	private IRefresh refresh1;
	private IRefresh refresh2;
	private IRefreshManager refreshManager;

	public void testDoesntCrashWithNoListeners() {
		replay();
		refreshManager.refresh();
	}

	public void testRefreshIsPropogatedToOtherRefreshers() {
		refresh1.refresh();
		refresh2.refresh();
		replay();
		refreshManager.addRefreshListener(refresh1);
		refreshManager.addRefreshListener(refresh2);
		refreshManager.refresh();
	}

	public void testRemoveRefreshStopsRefreshOccuring() {
		refresh1.refresh();
		refresh1.refresh();
		refresh2.refresh();
		replay();
		refreshManager.addRefreshListener(refresh1);
		refreshManager.addRefreshListener(refresh2);
		refreshManager.refresh();
		refreshManager.removeRefreshListener(refresh2);
		refreshManager.refresh();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		refresh1 = EasyMock.createMock(IRefresh.class);
		refresh2 = EasyMock.createMock(IRefresh.class);
		refreshManager = IRefreshManager.Utils.refreshManager(IMultipleListenerList.Utils.defaultList());

	}

	private void replay() {
		EasyMock.replay(refresh1, refresh2);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(refresh1, refresh2);
		super.tearDown();
	}
}
