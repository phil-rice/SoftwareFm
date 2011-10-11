package org.softwareFm.display.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.EditorIds;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.RippedEditorId;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public abstract class AbstractTextEditor<T extends Control> implements IEditor {
	private Composite content;
	protected AbstractTitleAndText<T> text;
	private IEditorCompletion completion;
	private ButtonParent buttonParent;
	private Runnable okRunnable;

	abstract protected AbstractTitleAndText<T> makeTitleAnd(Composite parent, CompositeConfig config);

	@Override
	public void edit(IDisplayer parent, final DisplayerDefn displayerDefn, ActionContext actionContext, final IEditorCompletion completion) {
		this.completion = completion;
		String title = IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, displayerDefn.title);
		String rawText = Strings.nullSafeToString(actionContext.dataGetter.getDataFor(displayerDefn.dataKey));
		text.setTitle(title);
		text.setText(rawText);
		okRunnable = new Runnable() {
			@Override
			public void run() {
				RippedEditorId rip = EditorIds.rip(displayerDefn.editorId);
				completion.ok(Maps.<String,Object>makeMap(rip.key, text.getText()));
				okRunnable = null;
			}
		};
		Swts.addAcceptCancel(buttonParent, actionContext.compositeConfig, okRunnable, new Runnable() {
			@Override
			public void run() {
				completion.cancel();
			}
		});
		
	}
	
	public AbstractTitleAndText<T> getText() {
		return text;
	}

	@Override
	public Control getControl() {
		return content;
	}
	
	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		text = makeTitleAnd(content, actionContext.compositeConfig);
		text.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (okRunnable != null)
					okRunnable.run();
			}
		});
		buttonParent = new ButtonParent(content, actionContext.compositeConfig, SWT.NULL);

		Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
		return content;
	}


	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}
}
