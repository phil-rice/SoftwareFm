/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer;

import org.eclipse.swt.SWT;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.collections.explorer.BrowserAndNavBar;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.history.IHistory;


public class BrowserAndNavBarTest extends SwtAndServiceTest{

	private IHistory<PlayItem> history;
	private BrowserAndNavBar browserAndNavBar;
	@SuppressWarnings("unused")
	private MemoryCallback<PlayItem> memoryCallback;

	public void testBrowserHistoryComboBackgroundIsSetToWhite() {//issue-3
		assertEquals(display.getSystemColor(SWT.COLOR_WHITE), browserAndNavBar.content.navNextHistoryPrev.getBackground());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		history = new History<PlayItem>();
		memoryCallback = ICallback.Utils.<PlayItem>memory();
		browserAndNavBar = new BrowserAndNavBar(shell, SWT.NULL, 4, CardDataStoreFixture.syncCardConfig(display), NavNextHistoryPrevConfig.<PlayItem>forTests(), service, history);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}