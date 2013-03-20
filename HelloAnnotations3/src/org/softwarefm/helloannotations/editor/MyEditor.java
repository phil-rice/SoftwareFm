package org.softwarefm.helloannotations.editor;

import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IElementStateListener;

public class MyEditor extends TextEditor {

	public MyEditor() {
		super();
		MyDocumentProvider provider = new MyDocumentProvider();
		setDocumentProvider(provider);
		provider.addElementStateListener(new IElementStateListener() {

			@Override
			public void elementMoved(Object originalElement, Object movedElement) {
				System.out.println("Editor.elementMoved" + originalElement.getClass());

			}

			@Override
			public void elementDirtyStateChanged(Object element, boolean isDirty) {
				System.out.println("Editor.elementDirtyStateChanged" + element.getClass());
				if (element instanceof IFileEditorInput) {
					System.out.println("  Its a fileeditorinput: " + element);
					IFileEditorInput input = (IFileEditorInput) element;
				}
			}

			@Override
			public void elementDeleted(Object element) {
				System.out.println("Editor.elementDeleted" + element.getClass());

			}

			@Override
			public void elementContentReplaced(Object element) {
				System.out.println("Editor.elementConentReplaced" + element.getClass());
			}

			@Override
			public void elementContentAboutToBeReplaced(Object element) {
				System.out.println("Editor.elementContentAboutToBeReplaced" + element.getClass());
			}
		});
	}

	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setSourceViewerConfiguration(new MySourceViewerConfiguration());
	}
}