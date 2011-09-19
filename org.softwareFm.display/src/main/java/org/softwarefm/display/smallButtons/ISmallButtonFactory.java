package org.softwarefm.display.smallButtons;

import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwarefm.display.displayer.ISmallDisplayer;
import org.softwarefm.display.impl.SmallButtonDefn;

public interface ISmallButtonFactory {

	ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config);

}
