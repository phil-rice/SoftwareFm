package org.softwareFm.display.actions;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;

public class TextEditAction implements IAction {
	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, ActionData actionData) {
		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				System.out.println("Result: " + t);
			}
		});
	}

}
