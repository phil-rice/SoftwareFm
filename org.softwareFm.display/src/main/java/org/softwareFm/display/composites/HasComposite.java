package org.softwareFm.display.composites;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class HasComposite extends Composite implements IHasComposite {

	public HasComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public Composite getComposite() {
		return this;
	}

}
