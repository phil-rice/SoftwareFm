package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavTitle;
import org.softwareFm.utilities.functions.Functions;

public class CardHolderTestWithTitleTest extends AbstractCardHolderTest {
	public void testDisplaysTitleWithTitleTextAndHasNoNavBarIfGotoUrlIsNull() {
		assertEquals("rootUrl", getTitleText());
	}

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		return (CardHolder) ICardHolder.Utils.cardHolderWithLayout(parent, "loadingtext", "some title", cardConfig, rootUrl, null);
	}

	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkTitleBasedOnUrlIsDisplayed("someUrl", "someUrl");
		checkTitleBasedOnUrlIsDisplayed("c", "a/b/c");
	}

	private void checkTitleBasedOnUrlIsDisplayed(String expected, String url) throws Exception {
		ICard cardWithLastSegmentAsTitle = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithLastSegmentAsTitle);
		assertEquals(cardConfig.cardTitleFn.apply(url), getTitleText());

		ICard cardWithUrlAsTitle =  cardConfig.cardFactory.makeCard(cardHolder, cardConfig.withTitleFn(Functions.<String, String> identity()), url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithUrlAsTitle);
		assertEquals(url, getTitleText());
	}

	String getTitleText() {
		NavTitle navTitle = (NavTitle) cardHolder.content.title;
		return navTitle.getText();
	}

}
