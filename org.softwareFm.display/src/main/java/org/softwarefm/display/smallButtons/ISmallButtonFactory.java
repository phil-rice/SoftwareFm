package org.softwarefm.display.smallButtons;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwarefm.display.impl.SmallButtonDefn;

public interface ISmallButtonFactory {

	IControlWithToggle create(Composite parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config);

}
