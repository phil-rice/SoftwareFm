package org.softwarefm.eclipse.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.AllComposite;

public class AllView extends SoftwareFmView<AllComposite> {

	@Override
	public AllComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		return new AllComposite(parent, container);
	}

}
