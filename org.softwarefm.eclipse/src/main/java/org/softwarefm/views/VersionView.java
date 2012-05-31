package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmActivator;

public class VersionView extends ViewPart {

	public VersionView() {
	}

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		new VersionPanel(parent, activator.getContainer());
	}

	@Override
	public void setFocus() {
	}

}
