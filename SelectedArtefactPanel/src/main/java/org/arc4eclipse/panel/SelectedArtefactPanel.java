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
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
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

	public static void main(String args[]) {
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
				final DisplayerContext context = new DisplayerContext(IImageFactory.Utils.imageFactory(), ISelectedBindingManager.Utils.noSelectedBindingManager(), repository);
				IDisplayContainerFactoryBuilder builder = IDisplayContainerFactoryBuilder.Utils.displayManager();
				builder.registerDisplayer(new DisplayText());
				builder.registerForEntity("entity", "text_key1", "Key1");
				builder.registerForEntity("entity", "text_key2", "Key2");
				IDisplayContainerFactory displayContainerFactory = builder.build();
				final SelectedArtefactPanel selectedArtefactPanel = new SelectedArtefactPanel(from, SWT.NULL, displayContainerFactory, context, "entity");
				from.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							selectedArtefactPanel.statusChanged("someUrl", RepositoryDataItemStatus.FOUND, Maps.<String, Object> makeMap("text_key1", "value1"), Maps.<String, Object> makeMap(RepositoryConstants.entity, "entity"));
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				});
				return selectedArtefactPanel;
			}
		});
	}

	@Override
	public String toString() {
		return entity + "/" + super.toString();
	}

}
