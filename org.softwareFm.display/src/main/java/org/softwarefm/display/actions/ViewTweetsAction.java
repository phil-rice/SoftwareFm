package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import org.softwareFm.display.IAction;
import org.softwareFm.display.displayer.IDisplayer;

public class ViewTweetsAction implements IAction {

	@Override
	@SuppressWarnings("rawtypes")
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, List<String> formalParameters, List<Object> actualParameters) throws Exception {
		Object data = actualParameters.get(0);
		if (data instanceof List && data != null){
			Object tweet = ((List)data).get(index);
			Desktop.getDesktop().browse(new URI("http://mobile.twitter.com/" + tweet));
		}
	}

}
