package org.softwareFm.display.editor;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.utilities.callbacks.ICallback;

public interface IEditor {

	void edit(Shell parent, EditorContext editorContext, List<String> formalParameters, List<Object> actualParameters,  ICallback<Object> onCompletion);

	void cancel();


}
