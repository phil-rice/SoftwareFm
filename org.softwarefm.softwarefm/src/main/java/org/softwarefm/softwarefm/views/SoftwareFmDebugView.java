package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmDebugComposite;

public class SoftwareFmDebugView extends SoftwareFmView<SoftwareFmDebugComposite> {

	@Override
	public SoftwareFmDebugComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new SoftwareFmDebugComposite(parent, container);
	}

}
