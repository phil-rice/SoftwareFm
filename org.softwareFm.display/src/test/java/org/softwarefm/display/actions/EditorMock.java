package org.softwareFm.display.actions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class EditorMock implements IEditor {

	private final String seed;
	public final AtomicInteger cancelCount = new AtomicInteger();
	public final List<Shell> parents = Lists.newList();
	public List<String> formalParams = Lists.newList();
	public List<Object> actualParams = Lists.newList();
	private IEditorCompletion onCompletion;
	public Label control;

	public EditorMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "EditorMock [seed=" + seed + "]";
	}
	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue) {
		this.onCompletion = completion;
		this.parents.add(parent);
		this.formalParams=actionData.formalParams;
		this.actualParams= actionData.actualParams;
	}

	@Override
	public void cancel() {
		cancelCount.incrementAndGet();
	}

	public void finish(String string) {
		try {
			onCompletion.ok(string);
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
		 this.control = new Label(actionContext.rightHandSide.getComposite(), SWT.NULL);
		 control.setText(seed);
		 return control;
	}

}
