package org.softwarefm.eclipse.annotations;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class DoubleClickAction implements IEditorActionDelegate {

	@Override
	public void run(IAction action) {
		System.out.println("Clicked Ruler" + action);

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("Selection changed" + selection);
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		System.out.println("Set active editor");
	}

}
