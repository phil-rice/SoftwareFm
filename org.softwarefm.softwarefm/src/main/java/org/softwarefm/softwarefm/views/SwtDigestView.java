package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SwtDigestComposite;

public class SwtDigestView extends SoftwareFmView<SwtDigestComposite>{

	@Override
	public SwtDigestComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new SwtDigestComposite(parent, container);
	}

}
