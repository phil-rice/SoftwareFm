package org.softwareFm.display.lists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.impl.DisplayerDefn;
import org.softwareFm.swtBasics.text.IButtonParent;

public interface IListEditor {

	 IButtonParent makeLineHasControl(DisplayerDefn displayDefn, CompositeConfig config, Composite listComposite, int index, Object value);
}
