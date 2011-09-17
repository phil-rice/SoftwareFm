package org.softwarefm.display.smallButtons;

import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwarefm.display.impl.SmallButtonDefn;

public interface ISmallButtonFactory {

	IControlWithToggle create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config);

}
