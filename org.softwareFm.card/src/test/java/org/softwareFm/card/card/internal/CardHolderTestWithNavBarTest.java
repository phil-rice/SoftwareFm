package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.navigation.internal.NavBar;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.history.IHistory;

public class CardHolderTestWithNavBarTest extends AbstractCardHolderTest {

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		ICardHolder cardHolder =ICardHolder.Utils.cardHolderWithLayout(shell, "loadingtext", "some title", cardConfig,rootUrl, ICallback.Utils.<String> memory());
		return (CardHolder) cardHolder;
	}

	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkNavBarWithSetCard(cardConfig);
	}
	
	public void testSetCardUpdatesHistory(){
		ICard card1 = makeAndSetCard(cardConfig, "one");
		ICard card2 = makeAndSetCard(cardConfig, "two");
		ICard card3 = makeAndSetCard(cardConfig, "three");
		checkHistory(card1, card2, card3);
	}

	private void checkHistory(ICard...cards) {
		NavBar title = (NavBar) cardHolder.getTitle();
		IHistory<String> history = title.getHistory();
		assertEquals( cards.length,history.size());
		for (int i = 0; i<cards.length;i++)
			assertEquals(cards[i].url(), history.getItem(i));
	}

	private void checkNavBarWithSetCard(CardConfig cardConfig) {
		makeAndSetCard(cardConfig);
		NavBar navbar = (NavBar) cardHolder.content.title;
		assertEquals(rootUrl, navbar.getRootUrl());
		assertEquals(rootUrl+"/someUrl", navbar.getCurrentUrl());
	}

}
