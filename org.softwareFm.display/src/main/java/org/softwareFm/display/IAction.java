package org.softwareFm.display;

import java.util.List;

import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;


public interface IAction {
	void execute(ActionContext actionContext, IDisplayer displayer, int index,ActionData actionData) throws Exception;
}
