package org.softwarefm.softwarefm.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ArtifactComposite;
import org.softwarefm.softwarefm.SoftwareFmActivator;

public class ArtifactView extends SoftwareFmView<ArtifactComposite> {

	@Override
	public ArtifactComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		ArtifactComposite artifactComposite = new ArtifactComposite(parent, container);
		artifactComposite.setLogger(SoftwareFmActivator.getDefault().getLogger());
		return artifactComposite;
	}

	@Override
	protected void configureToolbar(IToolBarManager toolBarManager) {
		configureToolbarWithProjectStuff(toolBarManager);
	}

}
