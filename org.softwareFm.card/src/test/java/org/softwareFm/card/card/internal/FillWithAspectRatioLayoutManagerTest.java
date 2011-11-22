package org.softwareFm.card.card.internal;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.collections.Lists;

public class FillWithAspectRatioLayoutManagerTest extends SwtIntegrationTest {

	public void testComputeSizeIfBothHintsAreDefaultUsesIdealSizeOfChildren() {
		checkComputeSize(SWT.DEFAULT, SWT.DEFAULT, new Point(1, 1), 0, new Point(0, 0));
		checkComputeSize(SWT.DEFAULT, SWT.DEFAULT, new Point(1, 1), 1, new Point(64, 64));
		checkComputeSize(SWT.DEFAULT, SWT.DEFAULT, new Point(1, 1), 2, new Point(128, 64));
		checkComputeSize(SWT.DEFAULT, SWT.DEFAULT, new Point(3, 2), 2, new Point(192, 64));
	}

	public void testComputeSizeIfWidthIsGivenIsWidthDivNumOfChildrenModifiedByWeights() {
		checkComputeSize(120, SWT.DEFAULT, new Point(1, 1), 0, new Point(120, 0));

		checkComputeSize(120, SWT.DEFAULT, new Point(1, 1), 1, new Point(120, 120));
		checkComputeSize(120, SWT.DEFAULT, new Point(2, 3), 1, new Point(120, 180));
		checkComputeSize(120, SWT.DEFAULT, new Point(3, 2), 1, new Point(120, 80));

		checkComputeSize(120, SWT.DEFAULT, new Point(1, 1), 10, new Point(120, 12));
		checkComputeSize(120, SWT.DEFAULT, new Point(2, 3), 10, new Point(120, 18));
		checkComputeSize(120, SWT.DEFAULT, new Point(3, 2), 10, new Point(120, 8));

	}

	public void testComputeSizeIfHeightIsGivenIsNumOfChildrenTimesHeightModifiedByWeights() {
		checkComputeSize(SWT.DEFAULT, 12, new Point(1, 1), 0, new Point(0, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(1, 1), 1, new Point(12, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(1, 1), 2, new Point(24, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(2, 3), 2, new Point(16, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(3, 2), 2, new Point(36, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(2, 3), 10, new Point(80, 12));
		checkComputeSize(SWT.DEFAULT, 12, new Point(3, 2), 10, new Point(180, 12));
	}

	public void testComputeSizeIfBothAreGivenIsBestFit() {
		checkComputeSize(120, 180, new Point(1, 1), 1, new Point(120, 120));
		checkComputeSize(120, 180, new Point(2, 1), 1, new Point(120, 60));
		checkComputeSize(120, 180, new Point(1, 2), 1, new Point(90, 180));

		checkComputeSize(120, 180, new Point(1, 1), 10, new Point(120, 12));
		checkComputeSize(120, 180, new Point(2, 1), 10, new Point(120, 6));
		checkComputeSize(120, 180, new Point(1, 2), 10, new Point(120, 24));

		checkComputeSize(120, 6, new Point(1, 1), 10, new Point(60, 6));
		checkComputeSize(120, 6, new Point(2, 1), 10, new Point(120, 6));
		checkComputeSize(120, 6, new Point(1, 2), 10, new Point(30, 6));
	}

	public void testLayoutStartsAtLeftOfClientAreaAndFillsWithHeightAndWidthBasedOnClientAreaHeightIgnoringSize() {
		checkLayout(60, 180, new Point(1, 1), 1, 176);
		checkLayout(60, 18, new Point(1, 1), 1, 14);

		checkLayout(60, 180, new Point(1, 2), 1, 88);
		checkLayout(60, 180, new Point(2,1), 1, 352);

		checkLayout(60, 184, new Point(1, 1), 10, 180);
		checkLayout(60, 18, new Point(1, 1), 1, 14);
		
		checkLayout(60, 180, new Point(1, 2), 1, 88);
		checkLayout(60, 180, new Point(2,1), 1, 352);
	}

	private void checkLayout(int width, int height, Point weights, int numOfControls, int childWidth) {
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setSize(width, height);
		try {
			FillWithAspectRatioLayoutManager layout = makeComponent(weights, numOfControls, composite);
			layout.layout(composite, true);
			Control[] children = composite.getChildren();
			Rectangle ca = composite.getClientArea();
			int x = ca.x;
			for (Control child : children) {
				assertEquals(x, child.getLocation().x);
				assertEquals(ca.y, child.getLocation().y);
				assertEquals(childWidth, child.getSize().x);
				assertEquals(ca.height, child.getSize().y);
				x += childWidth;
			}
		} finally {
			composite.dispose();
		}
	}

	// the points are just here to make the tests easier to read: don't want the test to be a long series of ints...
	private void checkComputeSize(int wHint, int hHint, Point weights, int numOfControls, Point expectedSize) {
		Composite composite = new Composite(shell, SWT.BORDER);
		try {
			FillWithAspectRatioLayoutManager layout = makeComponent(weights, numOfControls, composite);
			Point actual = layout.computeSize(composite, wHint, hHint, true);
			assertEquals(expectedSize, actual);
		} finally {
			composite.dispose();
		}
	}

	private FillWithAspectRatioLayoutManager makeComponent(Point weights, int numOfControls, Composite composite) {
		FillWithAspectRatioLayoutManager layout = new FillWithAspectRatioLayoutManager(weights.x, weights.y);
		composite.setLayout(layout);
		List<Object> controls = Lists.newList();
		for (int i = 0; i < numOfControls; i++) {
			Canvas control = new Canvas(composite, SWT.NULL);
			control.setSize(10, 5);
			controls.add(control);
		}
		return layout;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

}
