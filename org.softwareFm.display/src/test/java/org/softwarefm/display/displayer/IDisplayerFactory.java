package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.impl.DisplayerDefn;

public interface IDisplayerFactory {

	IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig);

}
