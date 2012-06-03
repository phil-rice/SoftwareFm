package org.softwarefm.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ProjectComposite;

public class ProjectView extends SoftwareFmView<ProjectComposite> {
	@Override
	public ProjectComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ProjectComposite(parent, container);
	}

}
