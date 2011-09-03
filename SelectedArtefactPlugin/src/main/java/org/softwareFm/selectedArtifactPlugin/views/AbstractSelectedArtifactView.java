package org.softwareFm.selectedArtifactPlugin.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.selectedArtifact.plugin.Activator;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

abstract public class AbstractSelectedArtifactView extends ViewPart {

	public static final String ID = "org.softwareFm.selectedArtifactPlugin.views.AbstractSelectedArtifactView";
	private IDisplayContainer displayContainer;

	public AbstractSelectedArtifactView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		Activator activator = Activator.getDefault();
		displayContainer = activator.makeDisplayContainer(parent, getViewName(), getEntityName());
		activator.getRepository().addStatusListener(displayContainer);
	}

	@Override
	public void dispose() {
		Activator.getDefault().getRepository().removeStatusListener(displayContainer);
		super.dispose();
	}

	abstract protected String getEntityName();

	abstract protected String getViewName();

	@Override
	public void setFocus() {
	}

}