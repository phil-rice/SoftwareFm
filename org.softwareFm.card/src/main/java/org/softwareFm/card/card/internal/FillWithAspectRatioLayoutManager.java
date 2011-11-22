package org.softwareFm.card.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.display.swt.Swts;

/** Horizonal laid out components. Fills from left. Each control is set to height, with aspect ratio determining the width */
public class FillWithAspectRatioLayoutManager extends Layout {

	private final int widthWeight;
	private final int heightWeight;
	private static int globalId;
	private final int id = globalId++;

	public FillWithAspectRatioLayoutManager(int widthWeight, int heightWeight) {
		this.widthWeight = widthWeight;
		this.heightWeight = heightWeight;
	}

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Control[] children = composite.getChildren();
		int noOfChildren = children.length;
		if (wHint == SWT.DEFAULT)
			if (hHint == SWT.DEFAULT) {
				int idealHeight = 0;
				int idealWidth = 0;
				for (Control control : children)
					idealHeight = Math.max(idealHeight, control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				idealWidth = heightToWidth(idealHeight) * noOfChildren;
				return new Point(idealWidth, idealHeight);
			} else {
				int width = heightToWidth(hHint) * noOfChildren;
				return new Point(width, hHint);
			}
		else if (hHint == SWT.DEFAULT) {
			if (noOfChildren == 0)
				return new Point(wHint, 0);
			int height = widthToHeight(wHint)/noOfChildren;
			return new Point(wHint, height);
		} else {
			int heightForwHint = widthToHeight(wHint / noOfChildren);
			int clippedheight = Math.min(heightForwHint, hHint);
			int widthForClippedHeight = heightToWidth(clippedheight);
			int width = Math.min(wHint / noOfChildren, widthForClippedHeight);
			int height = widthToHeight(width);
			Point result = new Point(width*noOfChildren, height);
			return result;
		}
	}

	private int widthToHeight(int wHint) {
		return wHint * heightWeight / widthWeight;
	}

	private int heightToWidth(int hHint) {
		return hHint * widthWeight / heightWeight;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		Rectangle clientArea = composite.getClientArea();
		System.out.println("FWAR " + id + " " + Swts.boundsUpToShell(composite) + " clientAreas: " + Swts.clientAreasUpToShell(composite));
		int height = clientArea.height;
		int width = heightToWidth(height);
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
