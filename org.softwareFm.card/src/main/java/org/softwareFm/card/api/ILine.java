package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.composites.IHasControl;

public interface ILine extends IHasControl{

	void addSelectedListener(Listener listener);
	
	void setWidth(int width, int titleWidth);
	
	int preferredTitleWidth();
	
}
