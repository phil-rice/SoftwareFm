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
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer,int index, ActionData actionData) throws Exception {
		List<Object> actualParameters = actionData.actualParams;
		Object data = actualParameters.get(0);
		if (data instanceof List && data != null){
			Object tweet = ((List)data).get(index);
			Desktop.getDesktop().browse(new URI("http://mobile.twitter.com/" + tweet));
		}
	}

}
