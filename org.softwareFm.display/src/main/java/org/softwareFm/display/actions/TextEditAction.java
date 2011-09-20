package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.IAction;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;

public class TextEditAction implements IAction {
	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, List<String> formalParameters, List<Object> actualParameters) {
		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", formalParameters, actualParameters, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				System.out.println("Result: " + t);
			}
		});
	}

}
