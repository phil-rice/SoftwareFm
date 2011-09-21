package org.softwareFm.display.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.collections.Lists;

public class ListDeleteAction implements IAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		if (MessageDialog.openConfirm(displayer.getControl().getShell(), "Delete", "delete")) {
			String key = displayerDefn.dataKey;
			List<String> currentList =Lists.nullSafe((List<String>) actionContext.dataGetter.getDataFor(key));
			List<String> newList = Lists.remove(currentList, index);
			if (newList.size()==0)
				newList = Arrays.asList(" ");
			actionContext.updateStore.update(actionData, key, newList.toArray(new String[0]));
		}
	}

}
