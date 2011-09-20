package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.IAction;
import org.softwareFm.display.displayer.IDisplayer;

public class ListDeleteAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer,int index,  List<String> formalParameters, List<Object> actualParameters) {
		System.out.println("Deleteing: "+index +","+ formalParameters +", " + actualParameters);
	}

}
