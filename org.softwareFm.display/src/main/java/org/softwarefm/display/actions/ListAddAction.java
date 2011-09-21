package org.softwareFm.display.actions;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;

public class ListAddAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, ActionData actionData) {
		System.out.println("Adding to: "+index +","+ actionData.formalParams+", " +actionData. actualParams);
	}

}
