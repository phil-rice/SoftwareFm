package org.softwareFm.display.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public abstract class AbstractTextEditor<T extends Control> implements IEditor {
	private Composite composite;
	protected  AbstractTitleAndText<T> text;
	private IEditorCompletion completion;

	abstract protected AbstractTitleAndText<T> makeTitleAnd(Composite parent, CompositeConfig config);
	

	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue) {
		this.completion = completion;
		text.setTitle(IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, displayerDefn.title));
		text.setText( Strings.nullSafeToString(initialValue));
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void createControl(ActionContext actionContext) {
		composite = new Composite(actionContext.rightHandSide.getComposite(), SWT.NULL);
		text = makeTitleAnd(composite, actionContext.compositeConfig);
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

}
