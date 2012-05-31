package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;

public class DigestView extends SoftwareFmView<DigestPanel> {
	@Override
	public DigestPanel makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new DigestPanel(parent, container);
	}

}
