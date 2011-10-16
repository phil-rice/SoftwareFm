package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;

public class Loading implements IHasControl {

	static class LoadingComposite extends Composite {
		public LoadingComposite(Composite parent) {
			super(parent, SWT.NULL);
		}
	}

	private final LoadingComposite content;

	public Loading(Composite parent) {
		content = new LoadingComposite(parent);

	}

	@Override
	public Control getControl() {
		return content;
	}

}
