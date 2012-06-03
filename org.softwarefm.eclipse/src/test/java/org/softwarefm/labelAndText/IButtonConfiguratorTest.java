package org.softwarefm.labelAndText;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.resources.ResourceGetterMock;
import org.softwarefm.utilities.runnable.Runnables;
import org.softwarefm.utilities.runnable.Runnables.CountRunnable;

public class IButtonConfiguratorTest extends SwtTest {

	private SoftwareFmContainer<?> container;

	public void testOk() {
		CountRunnable ok = Runnables.count();
		IButtonConfigurator.Utils.ok(ok).configure(shell, container);
		Control[] children = shell.getChildren();
		assertEquals(1, children.length);
		checkButton((Button) children[0], ok, SwtConstants.okButton);
	}

	public void testOkCancel() {
		CountRunnable ok = Runnables.count();
		CountRunnable cancel = Runnables.count();
		IButtonConfigurator.Utils.okCancel(ok, cancel).configure(shell, container);
		Control[] children = shell.getChildren();
		assertEquals(2, children.length);
		checkButton((Button) children[0], cancel, SwtConstants.cancelButton);
		checkButton((Button) children[1], ok, SwtConstants.okButton);
	}

	private void checkButton(Button button, CountRunnable count, String key) {
		assertEquals(0, count.getCount());
		Swts.Buttons.press(button);
		assertEquals(1, count.getCount());
		assertEquals(key, button.getData());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new SoftwareFmContainer<Object>(new ResourceGetterMock(SwtConstants.okButton, "OkName", SwtConstants.cancelButton, "CancelName"), ISelectedBindingManager.Utils.noManager());
	}
}
