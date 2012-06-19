package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.LinkComposite;

public class DebugLinkView extends SoftwareFmView<LinkComposite>{

	@Override
	public LinkComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new LinkComposite(parent, container);
	}

}
