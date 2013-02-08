package org.softwarefm.core.swt;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwarefm.utilities.lifecycle.IDispose;

public class HasComposite implements IHasComposite, IDispose {

	private final Composite content;

	public HasComposite(Composite parent, ImageRegistry imageRegistry) {
		this.content = makeComposite(parent, imageRegistry);
	}
	public HasComposite(Composite parent) {
		this(parent, null);
	}

	protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
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
	public void setLayoutData(Object layoutData) {
		content.setLayoutData(layoutData);
	}
}
