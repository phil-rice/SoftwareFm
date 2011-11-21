package org.softwareFm.card.navigation.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.junit.Test;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.internal.NavCombo;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;

public class NavComboTest extends SwtIntegrationTest {

	private CardConfig cardConfig;
	private MemoryCallback<String> memory;
	private NavCombo nav;
	private Combo navControl;

	public void testSetDropdownItemsPopulatesCombo(){
		List<String> list = Arrays.asList("1", "2", "3");
		nav.setDropdownItems(list);
		assertEquals(list, Arrays.asList(navControl.getItems()));
	}
	
	@Test
	public void testGetSelectedUrlIsRootUrlPlusSelected() {
		List<String> list = Arrays.asList("1", "2", "3");
		nav.setDropdownItems(list);
		navControl.select(1);
		assertEquals(Arrays.asList(), memory.getResult());
		navControl.notifyListeners(SWT.Selection,new Event());
		assertEquals(Arrays.asList(CardDataStoreFixture.url+"/" + 2), memory.getResult());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		memory = ICallback.Utils.memory();
		nav = new NavCombo(shell, cardConfig, CardDataStoreFixture.url, "1a", memory);
		navControl = (Combo) nav.getControl();
	}
}