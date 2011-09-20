package org.softwareFm.display.actions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class EditorMock implements IEditor {

	private final String seed;
	public final AtomicInteger cancelCount = new AtomicInteger();
	public final List<Shell> parents = Lists.newList();
	public final List<List<String>> formalParams = Lists.newList();
	public final List<List<Object>> actualParams = Lists.newList();
	private ICallback<Object> onCompletion;

	public EditorMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "EditorMock [seed=" + seed + "]";
	}
	
	@Override
	public void edit(Shell parent, EditorContext editorContext, List<String> formalParameters, List<Object> actualParameters, ICallback<Object> onCompletion) {
		this.onCompletion = onCompletion;
		this.parents.add(parent);
		this.formalParams.add(formalParameters);
		this.actualParams.add(actualParameters);
	}

	@Override
	public void cancel() {
		cancelCount.incrementAndGet();
	}

	public void finish(String string) {
		try {
			onCompletion.process(string);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
		
	}

}
