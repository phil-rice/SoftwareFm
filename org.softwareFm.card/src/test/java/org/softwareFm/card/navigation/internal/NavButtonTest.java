package org.softwareFm.card.navigation.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.junit.Test;
import org.softwareFm.card.navigation.internal.NavButton;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;

public class NavButtonTest extends SwtIntegrationTest {

	private MemoryCallback<String> memory;
	private NavButton navButton;
	private Label control;


	@Test
	public void testUrlIsTitle() {
		assertEquals("someUrl", control.getText());
	}
	
	public void testMouseUpEventCallsCallbackWIthUrl(){
		memory.assertNotCalled();
		control.notifyListeners(SWT.MouseUp, new Event());
		assertEquals("someUrl", memory.getOnlyResult());
	}

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memory = ICallback.Utils.memory();
		navButton = new NavButton(shell, "someUrl", memory);
		control = (Label) navButton.getControl();
	}
}
