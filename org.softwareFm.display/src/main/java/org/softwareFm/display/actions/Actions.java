package org.softwareFm.display.actions;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;

public class Actions {
	public static String getDataKey(DisplayerDefn displayerDefn, ActionData actionData) {
		if (actionData.actualParams.size() == 0)
			return displayerDefn.dataKey;
		else
			return actionData.formalParams.get(0);
	}
}
