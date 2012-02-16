/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.title.TitleSpec;

public class SoftwareFmCardTitleFunctionTest extends SwtTest {

	public void testCardTitleUsedLastSegmentOfUrlIfCardNameFieldNotSpecifiedInCollection() throws Exception {
		checkTitle("title", null, "a/b/c/title");
		checkTitle("title", null, "a/b/c/title");

		checkTitle("theName", "name", "a/b/c/title");
		checkTitle("theData", "data", "a/b/c/title");
		checkTitle("", "notIn", "a/b/c/title");
	}

	private void checkTitle(String expected, String cardNameField, String url) throws Exception {
		CardConfig raw = new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.mock());
		CardConfig cardConfig = raw.//
				withResourceGetterFn(IResourceGetter.Utils.mock(raw.resourceGetterFn.apply(null).//
						with(new ResourceGetterMock(CardConstants.cardNameFieldKey, cardNameField)))).//
				withTitleSpecFn(Functions.<ICardData, TitleSpec> constant(TitleSpec.noTitleSpec(display.getSystemColor(SWT.COLOR_WHITE))));

		Map<String, Object> rawData = Maps.stringObjectLinkedMap("name", "theName", "data", "theData");
		ICard card = ICardFactory.Utils.createCardWithLayout(shell, cardConfig, url, rawData);
		String actual = new SoftwareFmCardTitleFunction().apply(card);
		assertEquals(expected, actual);

	}

}