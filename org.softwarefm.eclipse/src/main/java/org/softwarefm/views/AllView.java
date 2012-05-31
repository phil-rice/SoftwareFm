package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmActivator;

public class AllView extends ViewPart {

	public AllView() {
	}

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		new AllPanel(parent, activator.getContainer());
	}

	@Override
	public void setFocus() {
	}

}
