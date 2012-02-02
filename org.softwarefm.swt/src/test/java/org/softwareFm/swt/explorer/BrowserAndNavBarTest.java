/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer;

import org.eclipse.swt.SWT;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.callbacks.MemoryCallback;
import org.softwareFm.common.history.History;
import org.softwareFm.common.history.IHistory;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.swt.swt.SwtAndServiceTest;
import org.softwareFm.swt.timeline.PlayItem;

public class BrowserAndNavBarTest extends SwtAndServiceTest {

	private IHistory<PlayItem> history;
	private BrowserAndNavBar browserAndNavBar;
	@SuppressWarnings("unused")
	private MemoryCallback<PlayItem> memoryCallback;
	private CardConfig cardConfig;

	public void testBrowserHistoryComboBackgroundIsSetToWhite() {// issue-3
		assertEquals(display.getSystemColor(SWT.COLOR_WHITE), browserAndNavBar.content.navNextHistoryPrev.getBackground());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		history = new History<PlayItem>();
		memoryCallback = ICallback.Utils.<PlayItem> memory();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		browserAndNavBar = new BrowserAndNavBar(shell, SWT.NULL, 4, cardConfig, NavNextHistoryPrevConfig.<PlayItem> forTests(), service, history);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}


}