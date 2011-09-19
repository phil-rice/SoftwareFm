package org.softwarefm.display.lists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.impl.DisplayerDefn;

public class ListEditorMock implements IListEditor {

	private final String seed;

	public ListEditorMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "ListEditorMock [seed=" + seed + "]";
	}

	@Override
	public IButtonParent makeLineHasControl(DisplayerDefn displayDefn, CompositeConfig config, Composite listComposite, int index, Object value) {
		throw new UnsupportedOperationException();
	}

}
