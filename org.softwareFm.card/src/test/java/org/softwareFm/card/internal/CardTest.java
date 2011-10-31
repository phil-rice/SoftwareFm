package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.maps.Maps;

public class CardTest extends SwtIntegrationTest {

	private final Map<String, Object> rawData = Maps.<String, Object> makeLinkedMap("a", 1, "b", 2);
	private final Map<String, Object> rawDataWithA3 = Maps.<String, Object> makeLinkedMap("a", 3,"b", 2);
	private CardConfig cardConfig;

	@Test
	public void testConstructor() {
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		assertSame(rawData, card.rawData());
		assertSame(cardConfig, card.cardConfig());
		Composite cardComposite = card.getComposite();
		assertSame(shell, cardComposite.getParent());
		assertSame(cardComposite, card.getControl());
		assertNull(card.cardType());
	}

	public void testKeyValuesAreTheRawDataModifiedByTheCardConfig() {
		Map<String, Object> result = Maps.<String,Object>makeMap("some", "result");
		MockKeyValueModifier mock = new MockKeyValueModifier(result);
		CardConfig cardConfig2 = cardConfig.withKeyValueModifiers(Arrays.<ICardDataModifier> asList(mock));
		Card card = new Card(shell, cardConfig2, "someUrl", rawData);
		assertEquals(result, card.data());
		assertEquals(rawData, mock.rawData);
	}

	public void testValueChangedDoesntAffectRawDataButUpdatesData() {
		Map<String, Object> copy = Maps.copyMap(rawData);
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		assertEquals(rawData, card.data());

		card.valueChanged("a", 3);
		assertEquals(rawDataWithA3, card.data());
		assertSame(rawData, card.rawData());
	}
	
	public void testRawDataNotMessedWithByConstructorOrValueChanged(){
		Map<String, Object> copy = Maps.copyMap(rawData);
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		card.valueChanged("a", 3);
		assertEquals(rawDataWithA3, card.data());
		assertEquals(rawData, card.rawData());
		
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
