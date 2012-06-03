package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.AllComposite;

public class AllView extends SoftwareFmView<AllComposite> {

	@Override
	public AllComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new AllComposite(parent, container);
	}

}
