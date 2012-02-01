/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.card.internal;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.history.IHistory;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.internal.CardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.internal.NavBar;

public class CardHolderTestWithNavBarTest extends AbstractCardHolderTest {

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(shell, cardConfig, Arrays.asList(rootUrl), ICallback.Utils.<String> memory());
		return (CardHolder) cardHolder;
	}

	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkNavBarWithSetCard(cardConfig);
	}

	public void testSetCardUpdatesHistory() {
		ICard card1 = makeAndSetCard(cardConfig, "one");
		ICard card2 = makeAndSetCard(cardConfig, "two");
		ICard card3 = makeAndSetCard(cardConfig, "three");
		checkHistory(card1, card2, card3);
	}

	private void checkHistory(ICard... cards) {
		NavBar title = (NavBar) cardHolder.getTitle();
		IHistory<String> history = title.getHistory();
		assertEquals(cards.length, history.size());
		for (int i = 0; i < cards.length; i++)
			assertEquals(cards[i].url(), history.getItem(i));
	}

	private void checkNavBarWithSetCard(CardConfig cardConfig) {
		makeAndSetCard(cardConfig);
		NavBar navbar = (NavBar) cardHolder.content.title;
		assertEquals(Arrays.asList(rootUrl), navbar.getRootUrls());
		assertEquals(rootUrl + "/someUrl", navbar.getCurrentUrl());
	}

}