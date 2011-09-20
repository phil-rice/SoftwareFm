package org.softwareFm.display;

import org.softwareFm.display.impl.LargeButtonDefn;
import org.softwareFm.display.impl.SmallButtonDefn;

public interface IDisplaySelectionListener {

	void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn);
}
