package org.softwarefm.display.actions;

import java.util.List;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwarefm.display.IAction;
import org.softwarefm.display.displayer.IDisplayer;

public class TextEditAction implements IAction {
	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer, List<String> formalParameters, List<Object> actualParameters) {
		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), "editor.text", formalParameters, actualParameters, new ICallback<Object>(){
			@Override
			public void process(Object t) throws Exception {
				System.out.println("Result: " + t);
			}});
	}


}
