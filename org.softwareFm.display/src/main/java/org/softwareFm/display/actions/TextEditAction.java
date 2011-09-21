package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class TextEditAction implements IAction {
	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {

		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				System.out.println("Result: " + t);
			}
		});
	}

}
