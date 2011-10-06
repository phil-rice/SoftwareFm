package org.softwareFm.display;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;

public interface IHasRightHandSide extends IHasComposite {
	
	void makeVisible(Control control);

	Control getVisibleControl();

}
