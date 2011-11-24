package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.SWT;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.history.IHistory;
import org.softwareFm.utilities.services.IServiceExecutor;


public class BrowserAndNavBarTest extends SwtIntegrationTest{

	private IServiceExecutor service;
	private IHistory<PlayItem> history;
	private BrowserAndNavBar browserAndNavBar;
	private MemoryCallback<PlayItem> memoryCallback;

	public void testBrowserHistoryComboBackgroundIsSetToWhite() {//issue-3
		assertEquals(display.getSystemColor(SWT.COLOR_WHITE), browserAndNavBar.content.navNextHistoryPrev.getBackground());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = IServiceExecutor.Utils.defaultExecutor();
		history = new History<PlayItem>();
		memoryCallback = ICallback.Utils.<PlayItem>memory();
		browserAndNavBar = new BrowserAndNavBar(shell, SWT.NULL, 4, CardDataStoreFixture.syncCardConfig(display), NavNextHistoryPrevConfig.<PlayItem>forTests(), service, history);
	}
	
	@Override
	protected void tearDown() throws Exception {
		service.shutdown();
		super.tearDown();
	}

}
