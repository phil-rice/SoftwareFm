package org.softwareFm.display.editor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class TextEditor implements IEditor {

	private TextDialog textDialog;

	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, ICallback<Object> onCompletion) {
		textDialog = new TextDialog(parent);
		String key = displayerDefn.dataKey;
		String initialValue = Strings.nullSafeToString(actionContext.dataGetter.getDataFor(key));
		String result = textDialog.open(editorContext.compositeConfig, "Value", Strings.nullSafeToString(initialValue), onCompletion);
		if (result != null){
			actionContext.updateStore.update(actionData, key, result);
		}
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
