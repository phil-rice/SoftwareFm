package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmActivator;

abstract public class SoftwareFmView<P extends SoftwareFmPanel> extends ViewPart {
	abstract public  P makePanel(Composite parent, SoftwareFmContainer<?> container);

	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		SoftwareFmContainer<?> container = activator.getContainer();
		makePanel(parent, container);
	}

	@Override
	public void setFocus() {
	}

}
