package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;

public class ClassAndMethodView extends SoftwareFmView<ClassAndMethodPanel> {

	@Override
	public ClassAndMethodPanel makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ClassAndMethodPanel(parent, container);
	}

}
