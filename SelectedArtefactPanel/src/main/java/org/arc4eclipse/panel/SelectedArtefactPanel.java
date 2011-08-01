package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class SelectedArtefactPanel extends Composite implements IStatusChangedListener {

	private final IArc4EclipseRepository repository;
	private final IDisplayContainer displayContainer;
	private final IDisplayManager displayManager;
	private final ISelectedBindingManager selectedBindingManager;
	private final Images images;
	private final String entity;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, String entity, IDisplayManager displayManager, IArc4EclipseRepository repository, ISelectedBindingManager selectedBindingManager, Images images) {
		super(parent, style);
		this.entity = entity;
		this.displayManager = displayManager;
		this.selectedBindingManager = selectedBindingManager;
		this.images = images;
		this.displayContainer = IDisplayContainer.Utils.displayContainer(this);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		this.repository = repository;
		Layout layout = new GridLayout();
		// layout.numColumns = 1;
		setLayout(layout);
		repository.addStatusListener(this);
	}

	@Override
	public void dispose() {
		repository.removeStatusListener(this);
		displayContainer.dispose();
		super.dispose();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> data, Map<String, Object> context) throws Exception {
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (entity.equals(actualEntity)) {
			BindingContext bindingContext = new BindingContext(repository, ITitleLookup.Utils.titleLookup(), images, url, data, context);
			displayManager.populate(displayContainer, bindingContext);
		}
	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				SelectedArtefactPanel selectedArtefactPanel = new SelectedArtefactPanel(from, SWT.NULL, RepositoryConstants.entityJarData, IDisplayManager.Utils.displayManager(), repository, ISelectedBindingManager.Utils.noSelectedBindingManager(), new Images(from.getDisplay()));
				return selectedArtefactPanel;
			}
		});
	}

	@Override
	public String toString() {
		return entity + "/" + super.toString();
	}

}
