package org.softwareFm.displayCore.api.impl;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.IDisplayContainerButtons;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class DisplayContainerButtons implements IDisplayContainerButtons {
	private final IResourceGetter resourceGetter;
	private final ImageRegistry imageRegistry;
	private final Composite compButtons;

	public DisplayContainerButtons(Composite parent, IResourceGetter resourceGetter, ImageRegistry imageRegistry) {
		compButtons = new Composite(parent, SWT.NULL) {
			@Override
			public String toString() {
				return DisplayContainerButtons.this.getClass().getSimpleName() + ":" + super.toString();
			}
		};
		compButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		this.resourceGetter = resourceGetter;
		this.imageRegistry = imageRegistry;
	}

	@Override
	public Composite getComposite() {
		return compButtons;
	}

	@Override
	public Control getControl() {
		return compButtons;
	}

	@Override
	public Composite getButtonComposite() {
		return compButtons;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return resourceGetter;
	}

	@Override
	public void buttonAdded() {
	}

}