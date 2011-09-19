package org.softwarefm.display.actions;

import java.util.List;

import org.softwarefm.display.IAction;
import org.softwarefm.display.displayer.IDisplayer;

public class ListDeleteAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer,int index,  List<String> formalParameters, List<Object> actualParameters) {
		System.out.println("Deleteing: "+index +","+ formalParameters +", " + actualParameters);
	}

}
