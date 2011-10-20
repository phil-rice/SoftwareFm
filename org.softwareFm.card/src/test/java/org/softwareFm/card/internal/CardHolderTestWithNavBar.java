package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.utilities.callbacks.ICallback;

public class CardHolderTestWithNavBar extends AbstractCardHolderTest {

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		CardHolder cardHolder = new CardHolder(shell, "loadingtext", "some title", cardConfig, "someRootUrl", ICallback.Utils.<String> memory());
		return cardHolder;
	}

	@Override
	protected Control getTitleControl() {
		return cardHolder.content.navBar.getControl();
	}

	@Override
	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkNavBarWithSetCard(cardConfig);
	}

	private void checkNavBarWithSetCard(CardConfig cardConfig) {
		makeAndSetCard(cardConfig);
		assertEquals("someRootUrl", cardHolder.content.navBar.getRootUrl());
		assertEquals("someRootUrl/someUrl", cardHolder.content.navBar.getCurrentUrl());
	}

}
