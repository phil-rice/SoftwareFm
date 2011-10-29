package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.IKeyValueListModifier;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.maps.Maps;

public class CardTest extends SwtIntegrationTest {

	private final Map<String, Object> rawData = Maps.<String, Object> makeLinkedMap("a", 1, "b", 2);
	private final List<KeyValue> rawDataAsKeyValues = Arrays.asList(new KeyValue("a", 1), new KeyValue("b", 2));
	private final List<KeyValue> rawDataAsKeyValuesWithA3 = Arrays.asList(new KeyValue("a", 3), new KeyValue("b", 2));
	private CardConfig cardConfig;

	@Test
	public void testConstructor() {
		ICard card = new Card(shell, cardConfig, "someUrl", rawData);
		assertSame(rawData, card.rawData());
		assertSame(cardConfig, card.cardConfig());
		Composite cardComposite = card.getComposite();
		assertSame(shell, cardComposite.getParent());
		assertSame(cardComposite, card.getControl());
		assertNull(card.cardType());
	}

	public void testKeyValuesAreTheRawDataModifiedByTheCardConfig() {
		List<KeyValue> result = Arrays.asList(new KeyValue("some", "result"));
		MockKeyValueModifier mock = new MockKeyValueModifier(result);
		CardConfig cardConfig2 = cardConfig.withKeyValueModifiers(Arrays.<IKeyValueListModifier> asList(mock));
		Card card = new Card(shell, cardConfig2, "someUrl", rawData);
		assertEquals(result, card.data());
		assertEquals(rawDataAsKeyValues, mock.rawList);
	}

	public void testValueChangedDoesntAffectRawDataButUpdatesData() {
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		assertEquals(rawDataAsKeyValues, card.data());

		card.valueChanged(card.data().get(0), 3);
		assertEquals(rawDataAsKeyValuesWithA3, card.data());
		assertSame(rawData, card.rawData());
	}

	public void testCardTypeIsBasedOnSlingResourceTypeInRawData() {
		checkCardType(null);
		checkCardType("a");
	}

	private void checkCardType(String expectedType) {
		Map<String, Object> map = expectedType == null ? rawData : Maps.with(rawData, CardConstants.slingResourceType, expectedType);
		Card card = new Card(shell, cardConfig, "someUrl", map);
		assertEquals(expectedType, card.cardType());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = new CardConfig(ICardFactory.Utils.mockCardFactory(), ICardDataStore.Utils.mock());
	}
}
