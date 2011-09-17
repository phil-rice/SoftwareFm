package org.softwarefm.display;

import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class SmallButtonFactory implements ISmallButtonFactory {

	@Override
	public IControlWithToggle create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new SimpleImageButton(parent,  config);
	}

}
