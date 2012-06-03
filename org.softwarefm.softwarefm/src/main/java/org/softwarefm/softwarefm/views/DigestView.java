package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.DigestComposite;

public class DigestView extends SoftwareFmView<DigestComposite> {
	@Override
	public DigestComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new DigestComposite(parent, container);
	}

}
