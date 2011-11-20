package org.softwareFm.card.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Row;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

public class OkCancel implements IOkCancel {

	public final Button okButton;
	public final Button cancelButton;
	private boolean enabled = true;
	private final Composite content;
	private final Runnable onAccept;
	private final Runnable onCancel;
	private final CardConfig cardConfig;

	public OkCancel(Composite parent, CardConfig cardConfig, final Runnable onAccept, final Runnable onCancel) {
		this.cardConfig = cardConfig;
		this.onAccept = onAccept;
		this.onCancel = onCancel;
		content = new Composite(parent, SWT.NULL);
		cancelButton = addButton( DisplayConstants.buttonCancelTitle, onCancel);
		okButton = addButton( DisplayConstants.buttonOkTitle, onAccept);
		content.setLayout(Row.getHorizonalNoMarginRowLayout());
	}

	
	public Button addButton(String titleKey, Runnable runnable){
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		Button result = Swts.Button.makePushButton(content, resourceGetter, titleKey,runnable);
		return result;
		
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
		onAccept.run();
	}

	@Override
	public void cancel() {
		onCancel.run();
	}

}
