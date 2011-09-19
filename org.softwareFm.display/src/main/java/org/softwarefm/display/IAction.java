package org.softwarefm.display;

import java.util.List;

import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.displayer.IDisplayer;


public interface IAction {
	void execute(ActionContext actionContext, IDisplayer displayer, int index, List<String> formalParameters, List<Object> actualParameters) throws Exception;
}
