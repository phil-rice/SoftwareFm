package org.softwareFm.card.navigation;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.strings.Strings;

public class NavButton implements IHasControl {
	private final Button button;

	public NavButton(Composite parent, final String url, final ICallback<String> callbackToGotoUrl) {
		String title = Functions.call(Strings.lastSegmentFn("/"), url);
		button = Swts.makePushButton(parent, null, title, false, new Runnable() {
			@Override
			public void run() {
				ICallback.Utils.call(callbackToGotoUrl, url);
			}
		});
	}

	@Override
	public Control getControl() {
		return button;
	}
}
