package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.CodeComposite;

public class CodeView extends SoftwareFmView<CodeComposite> {

	@Override
	public CodeComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}

}
