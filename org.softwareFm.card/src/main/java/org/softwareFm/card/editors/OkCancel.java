package org.softwareFm.card.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts.Row;
import org.softwareFm.utilities.resources.IResourceGetter;

public class OkCancel implements IOkCancel {

	public final Button okButton;
	public final Button cancelButton;
	private boolean enabled = true;
	private final Composite content;
	private final Runnable onAccept;
	private final Runnable onCancel;

	public OkCancel(Composite parent, CardConfig cardConfig, final Runnable onAccept, final Runnable onCancel) {
		this.onAccept = onAccept;
		this.onCancel = onCancel;
		content = new Composite(parent, SWT.NULL);
		IResourceGetter resourceGetter = cardConfig.resourceGetter;
		cancelButton = org.softwareFm.display.swt.Swts.Button.makePushButton(content, resourceGetter, DisplayConstants.buttonCancelTitle, onCancel);
		okButton = org.softwareFm.display.swt.Swts.Button.makePushButton(content, resourceGetter, DisplayConstants.buttonOkTitle, onAccept);
		content.setLayout(Row.getHorizonalNoMarginRowLayout());
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

	public void ok() {
		onAccept.run();
	}

	public void cancel() {
		onCancel.run();
	}

}
