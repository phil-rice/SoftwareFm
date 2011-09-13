package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.DisplayerDefn;

public interface IDisplayer {

	IHasControl create(Composite largeButtonComposite, DisplayerDefn defn, int style);

}
