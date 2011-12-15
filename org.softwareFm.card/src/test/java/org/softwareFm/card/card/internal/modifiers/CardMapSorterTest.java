/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal.modifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.modifiers.internal.CardMapSorter;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.utilities.maps.Maps;

public class CardMapSorterTest extends SwtTest {

	private CardConfig cardConfig;
	private CardMapSorter sorter;
	private Map<String, Object> acb;
	private Map<String, Object> versionInput;
	private final String lowVersion = "v-1.1.33";
	private final String highVersion = "v-1.1.111";

	@Test
	public void testDoesntChangeInput() {
		Map<String, Object> originalInput = Maps.copyMap(acb);
		sorter.modify(cardConfig, "someUrl", acb);
		assertEquals(originalInput, acb);
	}

	public void testOutputIsEqualsToInputButNotSame() {
		checkEqualNotNotSame(sorter.modify(cardConfig, "someUrl", acb), acb);
	}

	public void testSortsMap() {
		Map<String, Object> modified = sorter.modify(cardConfig, "someUrl", acb);
		assertEquals(Arrays.asList("a", "b", "c"), new ArrayList<String>(modified.keySet()));
	}

	public void testSortsVersionsBasedOnVersionNUmber() {
		Map<String, Object> modifiedWithoutVersion = sorter.modify(cardConfig, "someUrl", versionInput);
		assertEquals(modifiedWithoutVersion, versionInput);
		assertEquals(Arrays.asList(highVersion, lowVersion), new ArrayList<String>(modifiedWithoutVersion.keySet()));

		Map<String, Object> modified = sorter.modify(cardConfig, "someUrl/v", versionInput);
		assertEquals(modified, versionInput);
		assertEquals(Arrays.asList(lowVersion, highVersion), new ArrayList<String>(modified.keySet()));
	}

	private void checkEqualNotNotSame(Map<String, Object> modified, Map<String, Object> input) {
		assertEquals(modified, input);
		assertNotSame(modified, input);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		sorter = new CardMapSorter("v");
		acb = Maps.stringObjectLinkedMap("a", 1, "c", 3, "b", 3);
		versionInput = Maps.stringObjectLinkedMap(highVersion, 1, lowVersion, 2);
	}
}