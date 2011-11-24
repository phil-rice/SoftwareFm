package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavTitle;
import org.softwareFm.utilities.functions.IFunction1;

public class CardHolderWithTitleTest extends AbstractCardHolderTest {
	public void testDisplaysLoadingWhenConstructed() {
		assertEquals("loading", getTitleText());
	}

	public void testNavBarOrTitleChangesWhenCardAppears() throws Exception {
		checkTitleBasedOnUrlIsDisplayed("someUrl", "someUrl");
		checkTitleBasedOnUrlIsDisplayed("c", "a/b/c");
	}

	private void checkTitleBasedOnUrlIsDisplayed(String expected, String url) throws Exception {
		ICard cardWithLastSegmentAsTitle = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, CardDataStoreFixture.data1a);
		assertEquals(cardConfig.cardTitleFn.apply(cardWithLastSegmentAsTitle), getTitleText());

		ICard cardWithUrlAsTitle = cardConfig.cardFactory.makeCard(cardHolder, cardConfig.withTitleFn(new IFunction1<ICard, String>() {
			@Override
			public String apply(ICard from) throws Exception {
				return "x" + from.url() + "x";
			}
		}), url, CardDataStoreFixture.data1a);
		cardHolder.setCard(cardWithUrlAsTitle);
		assertEquals("x" + url + "x", getTitleText());
	}

	@Override
	protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig) {
		return (CardHolder) ICardHolder.Utils.cardHolderWithLayout(parent, cardConfig, rootUrl, null);
	}

	String getTitleText() {
		NavTitle navTitle = (NavTitle) cardHolder.content.title;
		return navTitle.getText();
	}

}
