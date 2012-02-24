package org.softwareFm.swt.editors;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.composites.IHasControl;

public interface IDataCompositeWithFooter<T extends Control, F extends IHasControl> extends IDataComposite<T> {

	F getFooter();

}
