package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.MavenImportComposite;

public class MavenImportView extends SoftwareFmView<MavenImportComposite>{

	@Override
	public MavenImportComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new MavenImportComposite(parent, container);
	}

}
