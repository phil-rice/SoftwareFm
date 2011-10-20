package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.utilities.functions.Functions;

public class CardHolderTestWithTitle extends AbstractCardHolderTest {
	public void testDisplaysTitleWithLoadingTextAndHasNoNavBarIfGotoUrlIsNull() {
		assertEquals("loadingtext", cardHolder.content.lblTitle.getText());
		assertNull(cardHolder.content.navBar);
	}

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		return new CardHolder(parent, "loadingtext", "some title", cardConfig, "someRootUrl", null);
	}

	@Override
	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkTitleBasedOnUrlIsDisplayed("someUrl", "someUrl");
		checkTitleBasedOnUrlIsDisplayed("c", "a/b/c");
	}

	private void checkTitleBasedOnUrlIsDisplayed(String expected, String url) throws Exception {
		ICard cardWithLastSegmentAsTitle = cardFactory.makeCard(cardHolder, cardConfig, url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithLastSegmentAsTitle);
		assertEquals(cardConfig.cardTitleFn.apply(url), cardHolder.content.lblTitle.getText());

		ICard cardWithUrlAsTitle = cardFactory.makeCard(cardHolder, cardConfig.withTitleFn(Functions.<String, String> identity()), url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithUrlAsTitle);
		assertEquals(url, cardHolder.content.lblTitle.getText());
	}



	@Override
	protected Control getTitleControl() {
		 return cardHolder.content.lblTitle;
	}
}
