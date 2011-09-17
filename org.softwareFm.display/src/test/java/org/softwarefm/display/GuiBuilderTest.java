package org.softwarefm.display;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwarefm.display.actions.ActionMock;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.displayer.DisplayerMock;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.SmallButtonFactoryMock;
import org.softwarefm.display.smallButtons.SmallButtonStore;

public class GuiBuilderTest extends TestCase {

	private GuiBuilder builder;
	private ActionMock action1;
	private SmallButtonFactoryMock smallButtonFactory1;
	private DisplayerMock displayer;
	private ActionStore actionStore;

	public void testSmallButtonNoDisplayerDefns() {
		SmallButtonDefn smallButton = builder.smallButton("someId", "titleId", "smallButton.validId", "mainImageId");
		assertEquals("someId", smallButton.id);
		assertEquals("titleId", smallButton.titleId);
		assertEquals(null, smallButton.tooltip);
		assertEquals(smallButtonFactory1, smallButton.smallButtonFactory);
		assertEquals(null, smallButton.controlClickActionData);
		assertEquals(null, smallButton.controlClickAction);
	}

	public void testSmallButtonWithDisplayerDefns() {
		DisplayerDefn defn1 = new DisplayerDefn(displayer);
		DisplayerDefn defn2 = new DisplayerDefn(displayer);
		SmallButtonDefn smallButton = builder.smallButton("someId", "titleId", "smallButton.validId", "mainImageId", defn1, defn2);
		assertEquals("someId", smallButton.id);
		assertEquals(null, smallButton.tooltip);
		assertEquals(smallButtonFactory1, smallButton.smallButtonFactory);
		assertEquals(Arrays.asList(defn1, defn2), smallButton.defns);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		action1 = new ActionMock("1");
		actionStore = new ActionStore().action("action1", action1);
		smallButtonFactory1 = new SmallButtonFactoryMock("validId");
		displayer = new DisplayerMock("1");
		SmallButtonStore smallButtonStore = new SmallButtonStore().smallButton("smallButton.validId", smallButtonFactory1);
		builder = new GuiBuilder(null, null, smallButtonStore, null, actionStore, null);
	}

}
