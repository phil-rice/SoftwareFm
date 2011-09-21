package org.softwareFm.display.editor;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.utilities.callbacks.ICallback;

public interface IEditor {

	void edit(Shell parent, EditorContext editorContext, ActionContext actionContext, ActionData actionData, ICallback<Object> onCompletion);

	void cancel();

}
