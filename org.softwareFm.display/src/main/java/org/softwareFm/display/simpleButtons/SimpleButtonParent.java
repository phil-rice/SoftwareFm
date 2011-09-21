package org.softwareFm.display.simpleButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.Swts;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SimpleButtonParent implements IButtonParent{

	private final Composite content;

	public SimpleButtonParent(Composite parent, SoftwareFmLayout layout, int style) {
		this.content = new Composite(parent,style);
		content.setLayout(Swts.getHorizonalMarginRowLayout(3));
		}

	@Override
	public Composite getButtonComposite() {
		return content;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IResourceGetter getResourceGetter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void buttonAdded(IHasControl button) {
		
	}

	public int size() {
		return content.getChildren().length;
	}

}
