package org.softwaremf.helloannotations.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CodeAction implements IObjectActionDelegate {

	private Shell shell;

	public CodeAction() {

	}

	@Override
	public void run(IAction action) {
		TitleAreaDialog dialog = new TitleAreaDialog(shell);
		dialog.setTitle("Some title");
		dialog.setMessage("Some message");
		dialog.create();
		System.out.println("Executing code");
		try {
			if (dialog.open() == Window.OK) {
				System.out.println("Dialog over");
			}
		} finally {
			dialog.close();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
}
