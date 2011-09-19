package org.softwarefm.display.lists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.impl.DisplayerDefn;

public class NameAndValueListEditor implements IListEditor{

	@Override
	public IHasControl makeLineHasControl(DisplayerDefn displayDefn, CompositeConfig config, Composite listComposite, int index, Object value) {
		return null;
	}

}
