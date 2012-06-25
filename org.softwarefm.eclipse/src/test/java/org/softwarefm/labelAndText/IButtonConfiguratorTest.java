package org.softwarefm.labelAndText;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.eclipse.tests.SwtTest;
import org.softwarefm.utilities.resources.ResourceGetterMock;
import org.softwarefm.utilities.runnable.Runnables;
import org.softwarefm.utilities.runnable.Runnables.CountRunnable;

public class IButtonConfiguratorTest extends SwtTest {

	private SoftwareFmContainer<?> container;

	public void testOk() {
		CountRunnable ok = Runnables.count();
		IButtonConfigurator.Utils.ok(ok).configure(container, IButtonCreator.Utils.creator(shell, container.resourceGetter));
		Control[] children = shell.getChildren();
		assertEquals(1, children.length);
		checkButton((Button) children[0], ok, TextKeys.btnSharedOk);
	}

	public void testOkCancel() {
		CountRunnable ok = Runnables.count();
		CountRunnable cancel = Runnables.count();
		IButtonConfigurator.Utils.okCancel(ok, cancel).configure(container, IButtonCreator.Utils.creator(shell, container.resourceGetter));
		Control[] children = shell.getChildren();
		assertEquals(2, children.length);
		checkButton((Button) children[0], cancel, TextKeys.btnSharedCancel);
		checkButton((Button) children[1], ok, TextKeys.btnSharedOk);
	}

	private void checkButton(Button button, CountRunnable count, String key) {
		assertEquals(0, count.getCount());
		Swts.Buttons.press(button);
		assertEquals(1, count.getCount());
		IButtonConfig config = (IButtonConfig) button.getData();
		assertEquals(key, config.key());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = SoftwareFmContainer.makeForTests(display, new ResourceGetterMock(TextKeys.btnSharedOk, "OkName", TextKeys.btnSharedCancel, "CancelName"));
	}
}
