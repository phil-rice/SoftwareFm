package org.softwarefm.display;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SimpleButtonParent implements IButtonParent{

	private final Composite content;

	public SimpleButtonParent(Composite parent, SoftwareFmLayout layout, int style) {
		this.content = new Composite(parent,style);
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
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

}
