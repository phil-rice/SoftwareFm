/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.internal.NavTitle;

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
		return (CardHolder) ICardHolder.Utils.cardHolderWithLayout(parent, cardConfig, Arrays.asList(rootUrl), null);
	}

	String getTitleText() {
		NavTitle navTitle = (NavTitle) cardHolder.content.title;
		return navTitle.getText();
	}

}