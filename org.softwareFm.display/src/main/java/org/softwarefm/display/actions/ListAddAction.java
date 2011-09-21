package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class ListAddAction implements IAction {

	@Override
	@SuppressWarnings("unchecked")
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, final ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData);
		final List<String> currentList =Lists.nullSafe((List<String>) actionContext.dataGetter.getDataFor(key));
		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				String string = Strings.nullSafeToString(t);
				List<String> newList = Lists.append(currentList, string);
				actionContext.updateStore.update(actionData, key, newList.toArray(new String[0]));
			}
		}, "");
	}

}
