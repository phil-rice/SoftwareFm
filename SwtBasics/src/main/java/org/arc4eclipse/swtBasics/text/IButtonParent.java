package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;

public interface IButtonParent {

	Composite getButtonComposite();

	ImageRegistry getImageRegistry();

	IResourceGetter getResourceGetter();

	void buttonAdded();
}
