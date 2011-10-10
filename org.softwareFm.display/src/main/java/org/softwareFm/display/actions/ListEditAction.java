package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.collections.Lists;

public class ListEditAction implements IAction {

	@Override
	@SuppressWarnings("unchecked")
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, final int index, final ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		final List<String> currentList = Lists.nullSafe((List<String>) actionContext.dataGetter.getDataFor(key));
		String initialValue = currentList.get(index);

//		actionContext.editorFactory.displayEditor(displayer, "editor.text", displayerDefn, actionContext, actionData, new ICallback<Object>() {
//			@Override
//			public void process(Object t) throws Exception {
//				String string = Strings.nullSafeToString(t);
//				List<String> newList = new ArrayList<String>(currentList);
//				newList.set(index, string);
//				actionContext.updateStore.update(actionData, key, newList.toArray(new String[0]));
//			}
//		}, initialValue);

	}

}
