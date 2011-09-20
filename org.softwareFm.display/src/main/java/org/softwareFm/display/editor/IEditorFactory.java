package org.softwareFm.display.editor;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.utilities.callbacks.ICallback;


public interface IEditorFactory {
	IEditorFactory register(String name, IEditor editor); 
	 void displayEditor(Shell parent, String editorName, List<String> formalParameters, List<Object> actualParameters, final ICallback<Object> onCompletion) ;
	
}
