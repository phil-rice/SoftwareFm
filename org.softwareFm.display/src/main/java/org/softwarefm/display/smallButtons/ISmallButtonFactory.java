package org.softwareFm.display.smallButtons;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.swtBasics.text.IButtonParent;

public interface ISmallButtonFactory {

	ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config);

}
