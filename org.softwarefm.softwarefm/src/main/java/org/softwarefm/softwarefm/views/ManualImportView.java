package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ManualImportComposite;

public class ManualImportView extends SoftwareFmView<ManualImportComposite>{

	@Override
	public ManualImportComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ManualImportComposite(parent, container);
	}

}
