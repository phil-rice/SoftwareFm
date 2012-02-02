/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.junit.Test;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.swt.SwtTest;

public class CardTest extends SwtTest {

	private final Map<String, Object> rawData = Maps.<String, Object> makeImmutableMap("a", 1, "b", 2);
	private final Map<String, Object> rawDataWithA3 = Maps.<String, Object> makeImmutableMap("a", 3, "b", 2);
	private CardConfig cardConfig;

	@Test
	public void testConstructor() {
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		assertSame(rawData, card.rawData());
		assertSame(cardConfig, card.getCardConfig());
		Composite cardComposite = card.getComposite();
		assertSame(shell, cardComposite.getParent());
		assertSame(cardComposite, card.getControl());
		assertNull(card.cardType());
	}

	public void testKeyValuesAreTheRawDataModifiedByTheCardConfig() {
		Map<String, Object> result = Maps.<String, Object> makeMap("some", "result");
		MockKeyValueModifier mock = new MockKeyValueModifier(result);
		CardConfig cardConfig2 = cardConfig.withCardDataModifiers(mock);
		Card card = new Card(shell, cardConfig2, "someUrl", rawData);
		assertEquals(result, card.data());
		assertEquals(rawData, mock.rawData);
	}

	public void testValueChangedDoesntAffectRawDataButUpdatesData() {
		Card card = new Card(shell, cardConfig, "someUrl", rawData);
		assertEquals(rawData, card.data());

		card.valueChanged("a", 3);
		assertEquals(rawDataWithA3, card.data());
		assertSame(rawData, card.rawData());
	}

	public void testRawDataNotMessedWithByConstructorOrValueChanged() {
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
		cardConfig = CardDataStoreFixture.syncCardConfig(shell.getDisplay());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}
}