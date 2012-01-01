package org.softwareFm.display.okCancel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.ResourceGetterMock;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.runnable.Runnables.CountRunnable;

public class OkCancelTest extends SwtTest {

	private CountRunnable cancel;
	private CountRunnable ok;
	private OkCancel okCancel;

	public void testOKCallsRunnable() {
		checkCounts(0, 0);
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		checkCounts(1, 0);
	}

	public void testCancelCallsRunnable() {
		checkCounts(0, 0);
		okCancel.cancelButton.notifyListeners(SWT.Selection, new Event());
		checkCounts(0, 1);
	}

	public void testLabels() {
		assertEquals("OK-Title", okCancel.okButton.getText());
		assertEquals("Cancel-Title", okCancel.cancelButton.getText());
	}

	public void testOKButtonIsDisabledWhenSelected() {
		assertTrue(okCancel.okButton.getEnabled());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(okCancel.okButton.getEnabled());
	}

	public void testCannotSpamOkButton() {
		assertTrue(okCancel.okButton.getEnabled());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(okCancel.okButton.getEnabled());
		checkCounts(1, 0);

	}

	public void testSetEnabled() {
		checkOkEnabled(false);
		checkOkEnabled(true);
		checkOkEnabled(false);
		checkOkEnabled(true);
	}

	private void checkOkEnabled(boolean expected) {
		int startCount = ok.getCount();
		okCancel.setOkEnabled(expected);
		assertEquals(expected, okCancel.okButton.getEnabled());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		int endCount = ok.getCount();
		int expectedCount = expected ? startCount + 1 : startCount;
		assertEquals(expectedCount, endCount);
	}

	private void checkCounts(int okCount, int cancelCount) {
		assertEquals(ok.getCount(), okCount);
		assertEquals(cancel.getCount(), cancelCount);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cancel = Runnables.count();
		ok = Runnables.count();
		IResourceGetter resourceGetter = new ResourceGetterMock(DisplayConstants.buttonOkTitle, "OK-Title", DisplayConstants.buttonCancelTitle, "Cancel-Title");
		okCancel = new OkCancel(shell, resourceGetter, ok, cancel);
	}

}
