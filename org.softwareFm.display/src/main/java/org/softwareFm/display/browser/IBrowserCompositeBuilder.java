package org.softwareFm.display.browser;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.utilities.functions.IFunction1;

public interface IBrowserCompositeBuilder extends IBrowserComposite {
	IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> displayCreator);
}
