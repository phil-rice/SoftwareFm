package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.strings.Strings;

public class BrowseAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData);
		String param = Strings.nullSafeToString(actionContext.dataGetter.getDataFor(key));
		if (param != null) {
			try {
				Desktop.getDesktop().browse(new URI(param.toString()));
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}
