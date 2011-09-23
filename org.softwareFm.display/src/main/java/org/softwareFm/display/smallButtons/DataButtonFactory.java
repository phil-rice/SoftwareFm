package org.softwareFm.display.smallButtons;

import java.text.MessageFormat;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;

public class DataButtonFactory implements ISmallButtonFactory {

	@Override
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		if (smallButtonDefn.dataId == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "smallButtonDefn.dataId", smallButtonDefn));
		return new DataImageButton(parent, config, smallButtonDefn.dataId);
	}
}
