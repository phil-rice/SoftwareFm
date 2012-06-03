package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.softwarefm.SoftwareFmActivator;

abstract public class SoftwareFmView<C extends SoftwareFmComposite> extends ViewPart {
	abstract public  C makePanel(Composite parent, SoftwareFmContainer<?> container);

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
