package org.softwareFm.display.smallButtons;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;

public class SmallButtonFactory implements ISmallButtonFactory {

	@Override
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new SimpleImageButton(parent,  config);
	}

}
