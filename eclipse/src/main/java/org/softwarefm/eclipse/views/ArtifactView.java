package org.softwarefm.eclipse.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.ArtifactComposite;
import org.softwarefm.eclipse.SoftwareFmPlugin;

public class ArtifactView extends SoftwareFmView<ArtifactComposite> {

	@Override
	public ArtifactComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		ArtifactComposite artifactComposite = new ArtifactComposite(parent, container);
		artifactComposite.setLogger(SoftwareFmPlugin.getDefault().getLogger());
		return artifactComposite;
	}

	@Override
	protected void configureToolbar(IToolBarManager toolBarManager) {
		configureToolbarWithProjectStuff(toolBarManager);
	}

}
