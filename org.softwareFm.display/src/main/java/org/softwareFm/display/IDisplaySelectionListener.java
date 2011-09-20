package org.softwareFm.display;

import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.smallButtons.SmallButtonDefn;

public interface IDisplaySelectionListener {

	void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn);
}
