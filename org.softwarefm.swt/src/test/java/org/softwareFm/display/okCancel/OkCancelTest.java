package org.softwareFm.display.okCancel;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.runnable.Runnables.CountRunnable;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;

public class OkCancelTest extends SwtTest {

	private CountRunnable cancel;
	private CountRunnable ok;
	private OkCancel okCancel;
	private ImageRegistry imageRegistry;

	public void testOKCallsRunnable() {
		checkCounts(0, 0);
		Swts.Buttons.press(okCancel.okButton);
		checkCounts(1, 0);
	}

	public void testCancelCallsRunnable() {
		checkCounts(0, 0);
		Swts.Buttons.press(okCancel.cancelButton);
		checkCounts(0, 1);
	}

//	public void testLabels() {
//		assertEquals("OK-Title", okCancel.okButton.getText());
//		assertEquals("Cancel-Title", okCancel.cancelButton.getText());
//	}

	public void testOKButtonIsDisabledWhenSelected() {
		assertTrue(okCancel.okButton.getEnabled());
		Swts.Buttons.press(okCancel.okButton);
		assertFalse(okCancel.okButton.getEnabled());
	}

	public void testCannotSpamOkButton() {
		assertTrue(okCancel.okButton.getEnabled());
		Swts.Buttons.press(okCancel.okButton);
		assertFalse(okCancel.okButton.getEnabled());
		Swts.Buttons.press(okCancel.okButton);
		Swts.Buttons.press(okCancel.okButton);
		Swts.Buttons.press(okCancel.okButton);
		Swts.Buttons.press(okCancel.okButton);
		Swts.Buttons.press(okCancel.okButton);
		Swts.Buttons.press(okCancel.okButton);
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
		Swts.Buttons.press(okCancel.okButton);
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
		imageRegistry = new ImageRegistry(display);
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
		IFunction1<String, Image> imageFn = new IFunction1<String, Image>() {
			@Override
			public Image apply(String from) throws Exception {
				return imageRegistry.get(from);
			}
		};
		okCancel = new OkCancel(shell, resourceGetter, imageFn, ok, cancel);
	}

	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		imageRegistry.dispose();
	}
}
