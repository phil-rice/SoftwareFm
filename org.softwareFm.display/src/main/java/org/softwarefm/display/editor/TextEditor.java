package org.softwareFm.display.editor;

import java.util.Arrays;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class TextEditor implements IEditor {

	private TextDialog textDialog;

	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, ICallback<Object> onCompletion, Object initialValue) {
		textDialog = new TextDialog(parent);
		textDialog.open(editorContext.compositeConfig, "Value", Strings.nullSafeToString(initialValue), onCompletion);
	
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
		Editors.display("TextEditor", new TextEditor(), Arrays.asList("one", "two"), Arrays.<Object> asList("1", "2"), "Text");
	}

}
