package org.softwareFm.display.editor;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;

public interface IEditor {

	void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext,  ActionData actionData, ICallback<Object> onCompletion, Object initialValue);

	void cancel();

}
