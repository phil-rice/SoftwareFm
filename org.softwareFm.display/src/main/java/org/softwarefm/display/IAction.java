package org.softwareFm.display;

import java.util.List;

import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.displayer.IDisplayer;


public interface IAction {
	void execute(ActionContext actionContext, IDisplayer displayer, int index, List<String> formalParameters, List<Object> actualParameters) throws Exception;
}
