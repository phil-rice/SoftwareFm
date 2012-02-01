/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrev.NavNextHistoryPrevLayout;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.callbacks.MemoryCallback;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.history.History;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;

public class NavNextHistoryPrevTest extends SwtTest {

	private final static int height = 44;

	private History<Integer> history;
	private ImageRegistry imageRegistry;
	private MemoryCallback<Integer> memory;
	private NavNextHistoryPrevConfig<Integer> navNextHistoryPrevConfig;
	private NavNextHistoryPrev<Integer> nav;
	private Composite navComposite;

	@Test
	public void testComputeSize() {
		checkComputeSize(SWT.DEFAULT, SWT.DEFAULT);
		checkComputeSize(1, 2);
		checkComputeSize(100, 200);
	}

	public void testGetClientArea() {
		navComposite.setLocation(1000, 2000);
		navComposite.setSize(100, 200);
		Rectangle ca = navComposite.getClientArea();
		assertEquals(new Rectangle(2, 6, 100 - 2 - 4, 200 - 6 - 8), ca);
	}

	public void testLayout() {
		nav.setLayout(new NavNextHistoryPrev.NavNextHistoryPrevLayout<Integer>());
		navComposite.setSize(100, 200);
		Control[] children = navComposite.getChildren();
		assertEquals(3, children.length);
		Control prevControl = children[0];
		Control historyControl = children[1];
		Control nextControl = children[2];
		assertEquals(new Rectangle(2, 6, 20, height), prevControl.getBounds());
		assertEquals(new Rectangle(2 + 20, 6, 20, 23), historyControl.getBounds());// now you are asking why this isn't using height. and I don't know. I suspect it's because combo has internal stuff
		assertEquals(new Rectangle(2 + 20 + 20, 6, 20, height), nextControl.getBounds());
	}

	public void testGetHistory() {
		assertEquals(history, nav.getHistory());
	}

	public void testSetBackgroundUpdatesAllTheBackground() {
		Color color = new Color(shell.getDisplay(), 10, 20, 30);
		nav.setBackground(color);
		assertEquals(color, navComposite.getBackground());
		Control[] children = navComposite.getChildren();
		for (Control child : children)
			assertEquals(color, child.getBackground());
	}

	private void checkComputeSize(int wHint, int hHint) {
		NavNextHistoryPrevLayout<Integer> layout = new NavNextHistoryPrev.NavNextHistoryPrevLayout<Integer>();
		Point size = layout.computeSize(navComposite, wHint, hHint, true);
		int expectedWidth = 2 + 4 + 3 * 20;// left/right margin * 3 icons
		int expectedHeight = 44 + 6 + 8; // height + top/bottom margin
		assertEquals(new Point(expectedWidth, expectedHeight), size);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		history = new History<Integer>();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry = new ImageRegistry());
		memory = ICallback.Utils.memory();
		navNextHistoryPrevConfig = new NavNextHistoryPrevConfig<Integer>(height, Swts.imageFn(imageRegistry), Functions.<Integer> addToEnd("_"), memory);
		nav = new NavNextHistoryPrev<Integer>(shell, navNextHistoryPrevConfig.withMargins(2, 4, 6, 8).withNavIconWidth(20), history);
		navComposite = (Composite) nav.getControl();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		imageRegistry.dispose();
	}

}