package org.softwareFm.display.displayer;

import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.simpleButtons.IButtonParent;

public interface IDisplayer extends IHasControl, IButtonParent {

	void addClickListener(Listener listener);

	void highlight();

	void unhighlight();

}
