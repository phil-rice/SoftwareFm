package org.softwareFm.display.actions;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;

public class TextEditAction implements IAction {
	@Override
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, final ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		Object initialValue = actionContext.dataGetter.getDataFor(key);

		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				actionContext.updateStore.update(actionData, key, t);
			}
		}, initialValue);
	}

}
