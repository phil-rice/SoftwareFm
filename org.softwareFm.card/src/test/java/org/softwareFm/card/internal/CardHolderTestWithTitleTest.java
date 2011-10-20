package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.navigation.NavTitle;
import org.softwareFm.utilities.functions.Functions;

public class CardHolderTestWithTitleTest extends AbstractCardHolderTest {
	public void testDisplaysTitleWithLoadingTextAndHasNoNavBarIfGotoUrlIsNull() {
		assertEquals("loadingtext", getTitleText());
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
		assertEquals(cardConfig.cardTitleFn.apply(url),getTitleText());

		ICard cardWithUrlAsTitle = cardFactory.makeCard(cardHolder, cardConfig.withTitleFn(Functions.<String, String> identity()), url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithUrlAsTitle);
		assertEquals(url, getTitleText());
	}

	String getTitleText() {
		NavTitle navTitle = (NavTitle) cardHolder.content.title;
		Label label = (Label) navTitle.getControl();
		return label.getText();
	}

}
