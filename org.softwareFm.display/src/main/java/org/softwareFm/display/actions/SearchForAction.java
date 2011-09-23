package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;

public class SearchForAction implements IAction {

	@Override
	@SuppressWarnings("rawtypes")
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		Object data = actionContext.dataGetter.getDataFor(key);
		Desktop.getDesktop().browse(new URI("http://www.google.co.uk/search?q=" + data + "&hl=en"));
	}

}
