/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer;

import org.eclipse.swt.SWT;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.crowdsource.utilities.history.History;
import org.softwareFm.crowdsource.utilities.history.IHistory;
import org.softwareFm.eclipse.usage.internal.ApiAndSwtTest;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.swt.timeline.PlayItem;

public class BrowserAndNavBarTest extends ApiAndSwtTest {

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
		browserAndNavBar = new BrowserAndNavBar(shell, SWT.NULL, 4, getLocalContainer(), cardConfig, NavNextHistoryPrevConfig.<PlayItem> forTests(), history);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

}