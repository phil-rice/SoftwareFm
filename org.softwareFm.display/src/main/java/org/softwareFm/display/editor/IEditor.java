package org.softwareFm.display.editor;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;

public interface IEditor extends IHasControl {

	Control createControl(ActionContext actionContext, ActionData actionData);

	void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue);

	void cancel();


}
