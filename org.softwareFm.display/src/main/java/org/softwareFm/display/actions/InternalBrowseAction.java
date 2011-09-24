package org.softwareFm.display.actions;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;

public class InternalBrowseAction implements IAction {

	@Override
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		String param = Actions.getString(actionContext, key, index);
		if (param != null)
			try {
				actionContext.internalBrowser.process(param);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
	}
}
