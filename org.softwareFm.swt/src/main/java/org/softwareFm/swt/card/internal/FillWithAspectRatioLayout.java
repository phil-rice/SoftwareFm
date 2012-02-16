/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/** Horizonal laid out components. Fills from left. Each control is set to height, with aspect ratio determining the width */
public abstract class FillWithAspectRatioLayout extends Layout {

	private static int globalId;
	@SuppressWarnings("unused")
	private final int id = globalId++;

	abstract protected int getWidthWeight(Composite composite);

	abstract protected int getHeightWeight(Composite composite);

	@Override
	public Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Control[] children = composite.getChildren();
		int noOfChildren = children.length;
		if (wHint == SWT.DEFAULT)
			if (hHint == SWT.DEFAULT) {
				int idealHeight = 0;
				int idealWidth = 0;
				for (Control control : children)
					idealHeight = Math.max(idealHeight, control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				idealWidth = heightToWidth(composite, idealHeight) * noOfChildren;
				return new Point(idealWidth, idealHeight);
			} else {
				int width = heightToWidth(composite, hHint) * noOfChildren;
				return new Point(width, hHint);
			}
		else if (hHint == SWT.DEFAULT) {
			if (noOfChildren == 0)
				return new Point(wHint, 0);
			int height = widthToHeight(composite, wHint) / noOfChildren;
			return new Point(wHint, height);
		} else {
			int heightForwHint = widthToHeight(composite, wHint / noOfChildren);
			int clippedheight = Math.min(heightForwHint, hHint);
			int widthForClippedHeight = heightToWidth(composite, clippedheight);
			int width = Math.min(wHint / noOfChildren, widthForClippedHeight);
			int height = widthToHeight(composite, width);
			Point result = new Point(width * noOfChildren, height);
			return result;
		}
	}

	private int widthToHeight(Composite composite, int wHint) {
		return wHint * getHeightWeight(composite) / getWidthWeight(composite);
	}

	private int heightToWidth(Composite composite, int hHint) {
		return hHint * getWidthWeight(composite) / getHeightWeight(composite);
	}

	@Override
	public void layout(Composite composite, boolean flushCache) {
		Rectangle clientArea = composite.getClientArea();
		// System.out.println("FWAR " + id + " " + Swts.boundsUpToShell(composite) + " clientAreas: " + Swts.clientAreasUpToShell(composite));
		int height = clientArea.height;
		int width = heightToWidth(composite, height);
		int x = clientArea.x;
		for (Control control : composite.getChildren()) {
			control.setLocation(x, clientArea.y);
			control.setSize(width, height);
			if (control instanceof Composite)
				((Composite) control).layout();
			x += width;
		}
	}

}