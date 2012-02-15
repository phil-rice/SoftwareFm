/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.navigation.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.junit.Test;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.callbacks.MemoryCallback;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.internal.NavCombo;
import org.softwareFm.swt.swt.SwtTest;

public class NavComboTest extends SwtTest {

	private CardConfig cardConfig;
	private MemoryCallback<String> memory;
	private NavCombo nav;
	private Combo navControl;

	public void testSetDropdownItemsPopulatesCombo() {
		List<String> list = Arrays.asList("1", "2", "3");
		nav.setDropdownItems(list);
		assertEquals(list, Arrays.asList(navControl.getItems()));
	}

	@Test
	public void testGetSelectedUrlIsRootUrlPlusSelected() {
		List<String> list = Arrays.asList("1", "2", "3");
		nav.setDropdownItems(list);
		navControl.select(1);
		assertEquals(Arrays.asList(), memory.getResults());
		navControl.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList(CardDataStoreFixture.url + "/" + 2), memory.getResults());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		memory = ICallback.Utils.memory();
		nav = new NavCombo(shell, cardConfig, CardDataStoreFixture.url, "1a", memory);
		navControl = (Combo) nav.getControl();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

}