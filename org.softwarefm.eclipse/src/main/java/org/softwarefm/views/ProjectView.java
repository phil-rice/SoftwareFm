package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;

public class ProjectView extends SoftwareFmView<ProjectPanel> {
	@Override
	public ProjectPanel makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ProjectPanel(parent, container);
	}

}
