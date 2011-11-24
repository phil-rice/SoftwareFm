package org.softwarefm.collections;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.ResourceGetterMock;

public class SoftwareFmCardTitleFunctionTest extends SwtIntegrationTest {

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
				withResourceGetterFn(IResourceGetter.Utils.mock(raw.resourceGetterFn.apply(null).with(new ResourceGetterMock(CardConstants.cardNameFieldKey, cardNameField)))).//
				withTitleSpecFn(Functions.<ICard,TitleSpec>constant(TitleSpec.noTitleSpec(display.getSystemColor(SWT.COLOR_WHITE))));

		Map<String, Object> rawData = Maps.stringObjectLinkedMap("name", "theName", "data", "theData");
		ICard card = ICardFactory.Utils.createCardWithLayout(shell, cardConfig, url, rawData);
		String actual = new SoftwareFmCardTitleFunction().apply(card);
		assertEquals(expected, actual);

	}

}
