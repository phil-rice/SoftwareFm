package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;

public class ViewTweetsAction implements IAction {

	@Override
	@SuppressWarnings("rawtypes")
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		Object data = actionContext.dataGetter.getDataFor(key);
		if (data instanceof List && data != null) {
			Object tweet = ((List) data).get(index);
			Desktop.getDesktop().browse(new URI("http://mobile.twitter.com/" + tweet));
		}
	}

}
