package org.softwareFm.display.editor;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;

public interface IEditorFactory {
	IEditorFactory register(String name, IEditor editor);

	void displayEditor(Shell parent, String editorName, DisplayerDefn displayerDefn, ActionContext actionContext, ActionData actionData, final ICallback<Object> onCompletion);

}
