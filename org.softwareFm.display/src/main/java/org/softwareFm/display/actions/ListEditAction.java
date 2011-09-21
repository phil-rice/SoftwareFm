package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class ListEditAction implements IAction {

	@Override
	@SuppressWarnings("unchecked")
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		String key = displayerDefn.dataKey;
		List<String> currentList =Lists.nullSafe((List<String>) actionContext.dataGetter.getDataFor(key));

		
		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				System.out.println("Result: " + t);
			}
		});

		
		if (currentList.size()>0 && currentList.get(currentList.size()-1).trim().length()==0)
			return;
		List<String> newList = Lists.append(currentList, " ");
		actionContext.updateStore.update(actionData, key, newList.toArray(new String[0]));
		System.out.println("Adding to: "+index +","+ actionData.formalParams+", " +actionData. actualParams +"\n...." + newList);
	}

}
