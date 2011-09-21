package org.softwareFm.display.actions;

import org.softwareFm.display.IAction;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;

public class ListDeleteAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer,int index,  ActionData actionData) {
		System.out.println("Deleteing: "+index +","+ actionData.formalParams+", " +actionData. actualParams);
	}

}
