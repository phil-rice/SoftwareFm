package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.navigation.NavBar;
import org.softwareFm.utilities.callbacks.ICallback;

public class CardHolderTestWithNavBarTest extends AbstractCardHolderTest {

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		CardHolder cardHolder = new CardHolder(shell, "loadingtext", "some title", cardConfig,rootUrl, ICallback.Utils.<String> memory());
		return cardHolder;
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
