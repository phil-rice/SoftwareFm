package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmActivator;

public class SoftwareFmDebugView extends ViewPart {

	public SoftwareFmDebugView() {
	}

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		SoftwareFmContainer<?> container = activator.getContainer();
		new SoftwareFmDebugPanel(parent, container);
	}

	@Override
	public void setFocus() {
	}

}
