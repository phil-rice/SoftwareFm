package org.softwareFm.card.navigation;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.history.History;

public class NavNextHistoryPrevTest extends SwtIntegrationTest {

	private final static int height = 44;

	private History<Integer> history;
	private ImageRegistry imageRegistry;
	private MemoryCallback<Integer> memory;
	private NavNextHistoryPrevConfig<Integer> navNextHistoryPrevConfig;
	private NavNextHistoryPrev<Integer> nav;
	private Composite navComposite;

	@Test
	public void testComputeSize() {
		checkComputeSize(nav, SWT.DEFAULT, SWT.DEFAULT);
		checkComputeSize(nav, 1, 2);
		checkComputeSize(nav, 100, 200);
	}

	public void testGetClientArea() {
		navComposite.setLocation(1000, 2000);
		navComposite.setSize(100, 200);
		Rectangle ca = navComposite.getClientArea();
		assertEquals(new Rectangle(2, 6, 100 - 2 - 4, 200 - 6 - 8), ca);
	}

	public void testLayout() {
		nav.layout();
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
	
	public void testGetHistory(){
		assertEquals(history, nav.getHistory());
	}
	
	public void testSetBackgroundUpdatesAllTheBackground(){
		Color color = new Color(shell.getDisplay(), 10, 20, 30);
		nav.setBackground(color);
		assertEquals(color, navComposite.getBackground());
		Control[] children = navComposite.getChildren();
		for (Control child: children)
			assertEquals(color, child.getBackground());
	}
	

	private void checkComputeSize(NavNextHistoryPrev<Integer> nav, int wHint, int hHint) {
		Point size = navComposite.computeSize(wHint, hHint);
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

}
