package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ClassAndMethodComposite;

public class ClassAndMethodView extends SoftwareFmView<ClassAndMethodComposite> {

	@Override
	public ClassAndMethodComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ClassAndMethodComposite(parent, container);
	}

}
