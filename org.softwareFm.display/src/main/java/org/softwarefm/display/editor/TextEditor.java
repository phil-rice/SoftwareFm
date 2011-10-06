package org.softwareFm.display.editor;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class TextEditor implements IEditor {

	private Composite composite;
	private TitleAndText text;
	private IEditorCompletion completion;

	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue) {
		this.completion = completion;
		text.setTitle(IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, displayerDefn.title));
		text.setText(Strings.nullSafeToString(initialValue));
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void createControl(ActionContext actionContext) {
		composite = new Composite(actionContext.rightHandSide.getComposite(), SWT.NULL);
		text = new TitleAndText	(actionContext.compositeConfig, composite, "", false);
		text.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				completion.ok(text.getText());
			}
		});
		Swts.makeAcceptCancelComposite(composite, SWT.NULL, actionContext.compositeConfig.resourceGetter, new Runnable() {
			@Override
			public void run() {
				completion.ok(text.getText());
			}
		}, new Runnable() {
			@Override
			public void run() {
				completion.cancel();
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
	}

	@Override
	public void cancel() {
		completion.cancel();
	}

	public static void main(String[] args) {
		Editors.display("TextEditor", new TextEditor(), Arrays.asList("one", "two"), Arrays.<Object> asList("1", "2"), "Text");
	}

}
