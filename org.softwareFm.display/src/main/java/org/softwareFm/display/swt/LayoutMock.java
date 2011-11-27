package org.softwareFm.display.swt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;


public class LayoutMock extends Layout{

	public int computeSize;
	public int layoutCount;

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		computeSize++;
		return new Point(0,0);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		layoutCount++;
	}

}
