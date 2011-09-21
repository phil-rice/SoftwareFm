package org.softwareFm.display.actions;

import java.util.Collections;
import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.collections.Lists;

public class ListAddAction implements IAction {

	@Override
	@SuppressWarnings("unchecked")
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, ActionData actionData) {
		String key = actionData.formalParams.get(0);
		List<String> currentList = (List<String>) actionData.actualParams.get(0);
		if (currentList==null)
			currentList = Collections.emptyList();
		if (currentList.size()>0 && currentList.get(currentList.size()-1).trim().length()==0)
			return;
		List<String> newList = Lists.append(currentList, " ");
		actionContext.updateStore.update(actionData, key, newList.toArray(new String[0]));
		System.out.println("Adding to: "+index +","+ actionData.formalParams+", " +actionData. actualParams +"\n...." + newList);
	}

}
