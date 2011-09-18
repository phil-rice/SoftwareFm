package org.softwarefm.display.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class TextEditor implements IEditor {

	private TextDialog textDialog;

	@Override
	public void edit(Shell parent, EditorContext editorContext, List<String> formalParameters, List<Object> actualParameters, ICallback<Object> onCompletion) {
		textDialog = new TextDialog(parent);
		textDialog.open(editorContext.compositeConfig, "Value", Strings.nullSafeToString(actualParameters.get(0)), onCompletion);
	}

	@Override
	public void cancel() {
		if (textDialog != null) {
			Shell shell = textDialog.getShell();
			if (shell != null)
				if (!shell.isDisposed())
					shell.close();
		}
	}

	public static void main(String[] args) {
		Editors.display("TextEditor", new TextEditor(), Arrays.asList("one", "two"), Arrays.<Object> asList("1", "2"));
	}

}
