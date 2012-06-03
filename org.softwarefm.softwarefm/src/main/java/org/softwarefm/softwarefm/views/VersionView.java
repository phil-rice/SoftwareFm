package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.VersionComposite;

public class VersionView extends SoftwareFmView<VersionComposite> {

	@Override
	public VersionComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new VersionComposite(parent, container);
	}

}
