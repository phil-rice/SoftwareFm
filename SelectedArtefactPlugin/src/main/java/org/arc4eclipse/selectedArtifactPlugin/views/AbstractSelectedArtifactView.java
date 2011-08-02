package org.arc4eclipse.selectedArtifactPlugin.views;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.panel.SelectedArtefactPanel;
import org.arc4eclipse.selectedArtifact.plugin.Activator;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

abstract public class AbstractSelectedArtifactView extends ViewPart {

	public static final String ID = "org.arc4eclipse.selectedArtifactPlugin.views.AbstractSelectedArtifactView";

	public AbstractSelectedArtifactView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		Activator activator = Activator.getDefault();
		final IArc4EclipseRepository repository = activator.getRepository();
		IDisplayContainerFactory displayContainerFactory = activator.getDisplayManager();
		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();
		IImageFactory imageFactory = activator.getImageFactory();
		DisplayerContext context = new DisplayerContext(imageFactory, selectedBindingManager, repository);
		new SelectedArtefactPanel(parent, SWT.NULL, displayContainerFactory, context, entity());
	}

	abstract protected String entity();

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void setFocus() {

	}

}