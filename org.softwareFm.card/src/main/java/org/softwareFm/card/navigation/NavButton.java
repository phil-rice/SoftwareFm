package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.strings.Strings;

public class NavButton implements IHasControl {
	private final Label label;

	public NavButton(Composite parent, final String url, final ICallback<String> callbackToGotoUrl) {
		String title = Functions.call(Strings.lastSegmentFn("/"), url);
		label = new Label(parent, SWT.NULL);
		label.setAlignment(SWT.LEFT);
		label.setText(title);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				ICallback.Utils.call(callbackToGotoUrl, url);
			}
		});
	}

	@Override
	public Control getControl() {
		return label;
	}
}
