package org.softwarefm.display.smallButtons;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.SmallButtonDefn;

public interface ISmallButtonFactory {

	IHasControl create(Composite parent, SmallButtonDefn smallButtonDefn, int style);

}
