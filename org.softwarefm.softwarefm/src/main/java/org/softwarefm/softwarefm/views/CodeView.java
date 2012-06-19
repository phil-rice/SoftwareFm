package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.CodeComposite;

public class CodeView extends SoftwareFmView<CodeComposite> {

	@Override
	public CodeComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}

}
