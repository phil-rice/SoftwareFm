package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class OkCancel implements IHasControl {

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
		cancelButton = Swts.makePushButton(content, resourceGetter, DisplayConstants.buttonCancelTitle, onCancel);
		okButton = Swts.makePushButton(content, resourceGetter, DisplayConstants.buttonOkTitle, onAccept);
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
	}

	public void setOkEnabled(boolean enabled) {
		this.enabled = enabled;
		okButton.setEnabled(enabled);
	}

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
