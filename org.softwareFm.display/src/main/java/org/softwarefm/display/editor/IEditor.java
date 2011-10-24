package org.softwareFm.display.editor;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;

public interface IEditor extends IHasControl {

	Control createControl(ActionContext actionContext);

	void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, IEditorCompletion completion);

	IButtonParent actionButtonParent();

}
