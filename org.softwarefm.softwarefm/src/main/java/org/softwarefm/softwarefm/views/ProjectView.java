package org.softwarefm.softwarefm.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ArtifactComposite;

public class ProjectView extends SoftwareFmView<ArtifactComposite> {

	@Override
	public ArtifactComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ArtifactComposite(parent, container);
	}

	@Override
	protected void configureToolbar(IToolBarManager toolBarManager) {
		configureToolbarWithProjectStuff(toolBarManager);
	}

}
