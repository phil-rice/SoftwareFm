package org.softwareFm.configuration.jar;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.smallButtons.ISmallButtonFactory;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.swtBasics.text.IButtonParent;

public class JarSimpleButtonFactory implements ISmallButtonFactory {

	@Override
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new JarSimpleImageButton(parent, config);
	}

}
