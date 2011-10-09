package org.softwareFm.display.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public abstract class AbstractTextEditor<T extends Control> implements IEditor {
	private Composite content;
	protected AbstractTitleAndText<T> text;
	private IEditorCompletion completion;
	private ButtonParent buttonParent;

	abstract protected AbstractTitleAndText<T> makeTitleAnd(Composite parent, CompositeConfig config);

	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, final IEditorCompletion completion, Object initialValue) {
		this.completion = completion;
		String title = IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, displayerDefn.title);
		String rawText = Strings.nullSafeToString(initialValue);
		text.setTitle(title);
		text.setText(rawText);
		Swts.addAcceptCancel(buttonParent, actionContext.compositeConfig, new Runnable() {
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
		
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Control createControl(ActionContext actionContext, ActionData actionData) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		text = makeTitleAnd(content, actionContext.compositeConfig);
		text.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				completion.ok(text.getText());
			}
		});
		buttonParent = new ButtonParent(content, actionContext.compositeConfig, SWT.NULL);

		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		return content;
	}

	@Override
	public void cancel() {
		completion.cancel();
	}

	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}
}
