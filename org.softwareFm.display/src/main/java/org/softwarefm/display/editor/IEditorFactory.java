package org.softwareFm.display.editor;

import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;

public interface IEditorFactory {
	IEditorFactory register(String name, IEditor editor);

	void cancel();

	void displayEditor(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer);

}
