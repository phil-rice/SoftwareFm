package org.softwareFm.display.editor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class TextEditor implements IEditor {

	private TextDialog textDialog;

	@Override
	public void edit(Shell parent, EditorContext editorContext, ActionData actionData, ICallback<Object> onCompletion) {
		List<Object> actualParameters = actionData.actualParams;
		textDialog = new TextDialog(parent);
		if (actualParameters.size()<1)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "params", getClass().getSimpleName()));
		String key = Strings.nullSafeToString(actionData.formalParams.get(0));
		String initialValue = Strings.nullSafeToString(actualParameters.get(0));
		String result = textDialog.open(editorContext.compositeConfig, "Value", Strings.nullSafeToString(initialValue), onCompletion);
		if (result != null){
			editorContext.updateStore.update(actionData, key, result);
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
