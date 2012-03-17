/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.okCancel.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.okCancel.IOkCancelForTests;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Row;

public class OkCancel implements IOkCancelForTests {

	public final Button okButton;
	public final Button cancelButton;
	private boolean enabled = true;
	private final Composite content;
	private final Runnable onAccept;
	private final Runnable onCancel;
	private final IResourceGetter resourceGetter;
	private final IFunction1<String, Image> imageFn;

	public OkCancel(Composite parent, IResourceGetter resourceGetter, IFunction1<String, Image> imageFn, final Runnable onAccept, final Runnable onCancel) {
		this.resourceGetter = resourceGetter;
		this.imageFn = imageFn;
		this.onAccept = onAccept;
		this.onCancel = onCancel;
		content = new Composite(parent, SWT.NULL);
		cancelButton = addButton(DisplayConstants.buttonCancelTitle, onCancel);
		okButton = addButton(DisplayConstants.buttonOkTitle, new Runnable() {
			@Override
			public void run() {
				if (okButton.isDisposed())
					return;
				if (okButton.isEnabled()) {
					ok();
				}
			}
		});
		content.setLayout(Row.getHorizonalNoMarginRowLayout());
	}

	public Label addImageButton(String name, Runnable runnable) {
		Image image = Functions.call(imageFn, name);
		if (image == null)
			throw new IllegalArgumentException(name);
		Label result = Swts.Buttons.makeImageButton(content, image, runnable);
		return result;
	}

	@Override
	public Button addButton(String titleKey, Runnable runnable) {
		Button result = Swts.Buttons.makePushButton(content, resourceGetter, titleKey, runnable);
		return result;
	}

	@Override
	public Control okButton() {
		return okButton;
	}

	@Override
	public Control cancelButton() {
		return cancelButton;
	}

	@Override
	public void setOkEnabled(boolean enabled) {
		this.enabled = enabled;
		okButton.setEnabled(enabled);
	}

	@Override
	public boolean isOkEnabled() {
		return enabled;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public void ok() {
		for (Control control : getComposite().getChildren())
			control.setEnabled(false);
		onAccept.run();
	}

	@Override
	public void cancel() {
		for (Control control : getComposite().getChildren())
			control.setEnabled(false);
		onCancel.run();
	}

	@Override
	public void pressButton(int index) {
		Swts.Buttons.press(content.getChildren()[index]);

	}

	public void pressButton(String title) {
		for (Control control : content.getChildren())
			if (control instanceof Button)
				if (((Button) control).getText().equals(title)) {
					control.notifyListeners(SWT.Selection, new Event());
					return;
				}
		throw new IllegalArgumentException(title);

	}

	@Override
	public Composite getComposite() {
		return content;
	}
}