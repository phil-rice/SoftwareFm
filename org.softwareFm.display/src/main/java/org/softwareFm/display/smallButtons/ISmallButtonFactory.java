package org.softwareFm.display.smallButtons;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;

public interface ISmallButtonFactory {

	ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config);

}
