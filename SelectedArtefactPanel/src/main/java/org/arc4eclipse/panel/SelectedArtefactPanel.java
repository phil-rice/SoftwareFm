package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.Images;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class SelectedArtefactPanel extends Composite implements IStatusChangedListener {

	private final String entity;
	private final IArc4EclipseRepository repository;
	private final Images images;
	private final IDisplayContainer displayContainer;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, IDisplayContainerFactory factory, DisplayerContext context, String entity) {
		super(parent, style);
		this.entity = entity;
		this.repository = context.repository;
		this.images = context.imageFactory.makeImages(getDisplay());
		displayContainer = factory.create(context, this, entity);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		Layout layout = new GridLayout();
		// layout.numColumns = 1;
		setLayout(layout);
		context.repository.addStatusListener(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> data, Map<String, Object> context) throws Exception {
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (entity.equals(actualEntity)) {
			BindingContext bindingContext = new BindingContext(repository, images, url, data, context);
			displayContainer.setValues(bindingContext);
		}
	}

	@Override
	public String toString() {
		return entity + "/" + super.toString();
	}

}
