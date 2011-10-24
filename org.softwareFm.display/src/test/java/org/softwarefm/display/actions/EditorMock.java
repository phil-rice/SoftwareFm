package org.softwareFm.display.actions;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;

public class EditorMock implements IEditor {

	public static final Map<String, Object> finishedData = Maps.<String, Object> makeMap("Finished", "It");

	private final String seed;
	public final List<IDisplayer> parents = Lists.newList();
	public List<DisplayerDefn> displayDefns = Lists.newList();
	public final List<ActionContext> actionsContexts = Lists.newList();
	private IEditorCompletion onCompletion;
	public Label control;
	private ButtonParent actionButtonParent;

	public EditorMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "EditorMock [seed=" + seed + "]";
	}

	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, IEditorCompletion completion) {
		this.onCompletion = completion;
		this.parents.add(parent);
		this.displayDefns.add(displayerDefn);
		this.actionsContexts.add(actionContext);
	}

	public void finish() {
		try {

			onCompletion.ok(finishedData);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public Control createControl(ActionContext actionContext) {
		Composite holder = actionContext.rightHandSide.getComposite();
		Composite composite = new Composite(holder, SWT.NULL);
		this.control = new Label(composite, SWT.NULL);
		control.setText(seed);
		this.actionButtonParent = new ButtonParent(composite, actionContext.compositeConfig, SWT.NULL);
		return control;
	}

	@Override
	public IButtonParent actionButtonParent() {
		return actionButtonParent;
	}

}
