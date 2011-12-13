/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation.internal;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.card.card.CardMock;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavBar.NavBarLayout;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.maps.Maps;

public class NavBarTest extends SwtTest {

	private CardConfig cardConfig;
	private NavBar nav;
	private Composite navComposite;
	private NavNextHistoryPrev<String> navHistoryPrev;

	@SuppressWarnings("unused")
	public void testCreatesNavCombosAndNavButtonsForTheUrl() {
		nav.setUrl(makeCard(CardDataStoreFixture.url + "/a/b/c"));
		Control[] children = navComposite.getChildren();
		assertEquals(8, children.length);
		Composite combo = (Composite) children[0]; // Actually NavNextHistoryPrevComposite
		Combo combo1 = (Combo) children[1];
		Label buttona = (Label) children[2];
		Combo combo2 = (Combo) children[3];
		Label buttonb = (Label) children[4];
		Combo combo3 = (Combo) children[5];
		Label buttonc = (Label) children[6];
		Combo combo4 = (Combo) children[7];

		assertEquals(3, combo.getChildren().length);// kindof saysing this is NextHistoryPrev
	}

	@SuppressWarnings("unused")
	public void testPopulatesComboWithDataFromCardStore() {
		nav.setUrl(makeCard(CardDataStoreFixture.url1a));
		Control[] children = navComposite.getChildren();
		assertEquals(4, children.length);
		Combo combo1 = (Combo) children[1];
		Label buttona = (Label) children[2];
		Combo combo2 = (Combo) children[3];
		assertEquals(Arrays.asList("data1a", "data1b", "data2a", "data2b", "data2c"), Arrays.asList(combo1.getItems()));
		assertEquals(Arrays.asList(), Arrays.asList(combo2.getItems()));
	}

	public void testButtonsHaveUrl() {
		nav.setUrl(makeCard(CardDataStoreFixture.url1a));
		Control[] children = navComposite.getChildren();
		Label buttona = (Label) children[2];
		assertEquals("1a", buttona.getText());
	}

	public void testGetRootUrl() {
		assertEquals(CardDataStoreFixture.url, nav.getRootUrl());
	}

	public void testGetHistoryIsSameHistoryAsInPrevHistoryNext() {
		assertSame(nav.getHistory(), navHistoryPrev.getHistory());
	}

	public void testComputeSize() {
		NavBarLayout layout = new NavBar.NavBarLayout();
		Point navControlSize = layout.computeSize(navComposite, SWT.DEFAULT, SWT.DEFAULT, true);
		assertEquals(new Point(cardConfig.leftMargin + 3*cardConfig.navIconWidth, cardConfig.titleHeight), navControlSize);

		int navIconWidth = cardConfig.navIconWidth;
		Label label = new Label(shell, SWT.NULL);
		label.setText("a");
		int aWidth = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

		checkComputeSize(CardDataStoreFixture.url, navControlSize.x + navIconWidth);// left margin + navcontrols + a / comb
		checkComputeSize(CardDataStoreFixture.url + "/a",  navControlSize.x + navIconWidth + aWidth + navIconWidth);// left margin + navcontrols + a / combo and the text for a and another combo
		checkComputeSize(CardDataStoreFixture.url + "/a/a/a", navControlSize.x + navIconWidth + aWidth + navIconWidth + aWidth + navIconWidth + aWidth + navIconWidth);
	}

	private void checkComputeSize(String url, int expectedWidth) {
		ICard card = makeCard(url);
		nav.setUrl(card);
		NavBarLayout layout = new NavBar.NavBarLayout();
		Point size = layout.computeSize(navComposite, SWT.DEFAULT, SWT.DEFAULT, true);
		assertEquals(new Point(expectedWidth, cardConfig.titleHeight), size);
	}

	private ICard makeCard(String url) {
		return new CardMock(null, cardConfig, url, Maps.stringObjectMap());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display).withMargins(5, 6, 7, 8);
		MemoryCallback<String> memory = ICallback.Utils.memory();
		nav = new NavBar(shell, cardConfig, CardDataStoreFixture.url, memory);
		navComposite = nav.getComposite();
		navHistoryPrev = nav.getNavHistoryPrev();
	}
}