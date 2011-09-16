package org.softwarefm.display;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class SmallButtonFactory implements ISmallButtonFactory {

	@Override
	public IControlWithToggle create(Composite parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new SimpleImageButton(parent,smallButtonDefn,  config);
	}

}
