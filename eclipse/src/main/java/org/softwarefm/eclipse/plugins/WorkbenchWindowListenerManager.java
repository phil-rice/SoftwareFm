package org.softwarefm.eclipse.plugins;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.utilities.exceptions.WrappedException;

public class WorkbenchWindowListenerManager {


	public WorkbenchWindowListenerManager(final ISelectedBindingManager<ITextSelection> selectionBindingManager) {
		final ISelectionListener listener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				System.out.println("Selection occured");
				if (selection instanceof ITextSelection)
					selectionBindingManager.selectionOccured((ITextSelection) selection);
				else
					selectionBindingManager.selectionOccured(null);
			}
		};
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			addToExistingWindows(listener, workbench);
			workbench.addWindowListener(new IWindowListener() {
				
				@Override
				public void windowOpened(IWorkbenchWindow window) {
					addToWindow(listener, window);
				}
				
				@Override
				public void windowDeactivated(IWorkbenchWindow window) {
				}
				
				@Override
				public void windowClosed(IWorkbenchWindow window) {
				}
				
				@Override
				public void windowActivated(IWorkbenchWindow window) {
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void addToExistingWindows(ISelectionListener listener, IWorkbench workbench) {
		IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[i];
			addToWindow(listener, workbenchWindow);
		}
	}

	private void addToWindow(ISelectionListener listener, IWorkbenchWindow workbenchWindow) {
		ISelectionService selectionService = workbenchWindow.getSelectionService();
		selectionService.addPostSelectionListener(listener);
	}
	
}
