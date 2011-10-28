package org.softwareFm.display.swt;

import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;

public class OkCancel {

	private final Button okButton;
	private boolean enabled = true;

	public OkCancel(IButtonParent buttonParent, CompositeConfig config, final Runnable onAccept, final Runnable onCancel) {
		IResourceGetter resourceGetter = config.resourceGetter;
		Composite buttonComposite = buttonParent.getButtonComposite();
		Button cancelButton = Swts.makePushButton(buttonComposite, resourceGetter, DisplayConstants.buttonCancelTitle, onCancel);
		okButton = Swts.makePushButton(buttonComposite, resourceGetter, DisplayConstants.buttonOkTitle, onAccept);
		cancelButton.setLayoutData(new RowData(config.layout.okCancelWidth, config.layout.okCancelHeight));
		okButton.setLayoutData(new RowData(config.layout.okCancelWidth, config.layout.okCancelHeight));
		buttonComposite.layout();
		buttonComposite.getParent().layout(); // only needed I think if there are no other buttons on the button composite
	}

	public void setOkEnabled(boolean enabled) {
		this.enabled = enabled;
		okButton.setEnabled(enabled);
	}

	public boolean isOkEnabled() {
		return enabled;
	}

}
