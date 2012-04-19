/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.okCancel;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.resources.ResourceGetterMock;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.crowdsource.utilities.runnable.Runnables.CountRunnable;
import org.softwareFm.images.BasicImageRegisterConfigurator;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.okCancel.internal.OkCancel;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.swt.Swts;

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

	// public void testLabels() {
	// assertEquals("OK-Title", okCancel.okButton.getText());
	// assertEquals("Cancel-Title", okCancel.cancelButton.getText());
	// }

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