package org.softwarefm.helloannotations;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.exceptions.WrappedException;

public class AllWorkbenchDoer {
	public static void addPartListener(IWorkbench workbench, final IPartListener partListener) {
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
			if (window != null)
				window.getActivePage().addPartListener(partListener);
		workbench.addWindowListener(new IWindowListener() {
			@Override
			public void windowOpened(IWorkbenchWindow window) {
				window.getActivePage().addPartListener(partListener);
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
			}

			@Override
			public void windowClosed(IWorkbenchWindow window) {
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage != null)
					activePage.removePartListener(partListener);
			}

			@Override
			public void windowActivated(IWorkbenchWindow window) {

			}
		});
	}

	public static void forEveryEditorNow(IWorkbench workbench, final ICallback2<IFile, AbstractDecoratedTextEditor> editorCallback) {
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				IEditorReference[] editors = page.getEditorReferences();
				for (IEditorReference editorReference : editors) {
					IEditorPart editorPart = editorReference.getEditor(false);
					call(editorCallback, editorPart);
				}
			}
		}
	}

	public static void forEveryEditorNowAndWhenOpens(IWorkbench workbench, final ICallback2<IFile, AbstractDecoratedTextEditor> editorCallback) {
		forEveryEditorNow(workbench, editorCallback);
		addPartListener(workbench, new IPartListener() {
			@Override
			public void partOpened(IWorkbenchPart part) {
				dumpModel("partOpened", part);
				call(editorCallback, part);
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				dumpModel("partDeactivated", part);
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				dumpModel("partBroughtToTop", part);
			}

			@Override
			public void partActivated(IWorkbenchPart part) {
				dumpModel("partActivated", part);
			}

			private void dumpModel(final String string, final IWorkbenchPart part) {
				if (part instanceof AbstractDecoratedTextEditor) {
					IAnnotationModel annotationModel = ((AbstractDecoratedTextEditor) part).getDocumentProvider().getAnnotationModel(((AbstractDecoratedTextEditor) part).getEditorInput());
					System.out.println(string + "/" + annotationModel);
				}

			}
		});
	}

	private static void call(final ICallback2<IFile, AbstractDecoratedTextEditor> editorCallback, IWorkbenchPart part) {
		try {
			if (part instanceof AbstractDecoratedTextEditor) {
				IResource resource = ResourceUtil.getResource(((AbstractDecoratedTextEditor) part).getEditorInput());
				if (resource instanceof IFile)
					editorCallback.process((IFile) resource, (AbstractDecoratedTextEditor) part);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
