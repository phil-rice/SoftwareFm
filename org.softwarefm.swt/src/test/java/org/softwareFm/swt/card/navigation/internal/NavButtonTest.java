/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.navigation.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.junit.Test;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.swt.navigation.internal.NavButton;
import org.softwareFm.swt.swt.SwtTest;

public class NavButtonTest extends SwtTest {

	private MemoryCallback<String> memory;
	private NavButton navButton;
	private Label control;

	@Test
	public void testUrlIsTitle() {
		assertEquals("someUrl", control.getText());
	}

	public void testMouseUpEventCallsCallbackWIthUrl() {
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
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}