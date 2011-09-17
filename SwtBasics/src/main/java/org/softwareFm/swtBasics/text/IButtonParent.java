package org.softwareFm.swtBasics.text;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface IButtonParent {

	Composite getButtonComposite();

	ImageRegistry getImageRegistry();

	IResourceGetter getResourceGetter();

	void buttonAdded(IHasControl button);
}
