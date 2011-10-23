package org.softwareFm.display.simpleButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ButtonParent implements IButtonParent {
	private final Composite content;
	private final ImageRegistry imageRegistry;
	private final IResourceGetter resourceGetter;
	private final SoftwareFmLayout layout;

	public ButtonParent(Composite parent, CompositeConfig config, int style) {
		this.content =Swts.newComposite(parent, style, getClass().getSimpleName());
		imageRegistry = config.imageRegistry;
		resourceGetter = config.resourceGetter;
		layout = config.layout;
		setLayoutData();
	}

	@Override
	public Composite getButtonComposite() {
		return content;
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
	public void buttonAdded(IHasControl button) {
		button.getControl().setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
		setLayoutData();

	}

	private void setLayoutData() {
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
		content.layout();
		content.getParent().layout();
	}

	public int size() {
		return content.getChildren().length;
	}

}
