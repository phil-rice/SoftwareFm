package org.softwarefm.display;

import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwarefm.display.displayer.ISmallDisplayer;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;
import org.softwarefm.display.smallButtons.ImageButtonConfig;

public class JarSimpleButtonFactory implements ISmallButtonFactory {

	@Override
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new JarSimpleImageButton(parent, config);
	}

}
