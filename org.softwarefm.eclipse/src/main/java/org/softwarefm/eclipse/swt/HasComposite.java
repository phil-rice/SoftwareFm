package org.softwarefm.eclipse.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class HasComposite implements IHasComposite, IDispose {

	private final Composite content;

	public HasComposite(Composite parent) {
		this.content = makeComposite(parent);
	}

	protected Composite makeComposite(Composite parent) {
		return Swts.newCompositeWithDispose(parent, SWT.NULL, getClass().getSimpleName(), this);
	}

	public Control getControl() {
		return content;
	}

	public Composite getComposite() {
		return content;
	}

	public void dispose() {
	}

	public void layout() {
		getComposite().layout();
	}

	public void setLayout(Layout layout) {
		content.setLayout(layout);
	}
}
