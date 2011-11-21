package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.navigation.internal.NavBar;
import org.softwareFm.utilities.callbacks.ICallback;

public class CardHolderTestWithNavBarTest extends AbstractCardHolderTest {

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		ICardHolder cardHolder =ICardHolder.Utils.cardHolderWithLayout(shell, "loadingtext", "some title", cardConfig,rootUrl, ICallback.Utils.<String> memory());
		return (CardHolder) cardHolder;
	}

	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkNavBarWithSetCard(cardConfig);
	}

	private void checkNavBarWithSetCard(CardConfig cardConfig) {
		makeAndSetCard(cardConfig);
		NavBar navbar = (NavBar) cardHolder.content.title;
		assertEquals(rootUrl, navbar.getRootUrl());
		assertEquals(rootUrl+"/someUrl", navbar.getCurrentUrl());
	}

}
