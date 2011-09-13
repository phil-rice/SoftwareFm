package org.softwarefm.display.impl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.Tests;
import org.softwarefm.display.actions.ActionMock;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.displayer.DisplayerMock;

public class DisplayerDefnTest extends TestCase {

	private DisplayerMock displayer;
	private DisplayerDefn displayerDefn;
	private ActionMock action;

	public void testConstructor() {
		assertEquals(displayer, displayerDefn.displayer);
		assertEquals(null, displayerDefn.dataKey);
		assertEquals(null, displayerDefn.tooltip);
	}

	public void testData() {
		assertSame(displayerDefn, displayerDefn.data("dataKey"));
		assertEquals("dataKey", displayerDefn.dataKey);
	}

	public void testTooltip() {
		assertSame(displayerDefn, displayerDefn.tooltip("tooltip"));
		assertEquals("tooltip", displayerDefn.tooltip);

	}

	public void testButton() {
		assertSame(displayerDefn, displayerDefn.action("action1", "dataLink", "image1"));
		assertEquals(Arrays.asList(new ImageButtonDefn("image1", null, "dataLink", Maps.<SmallIconPosition, String> newMap(), action)), displayerDefn.defns);
	}

	public void testMustUseKnownButton() {
		displayerDefn.action("action1", "dataLink", "image1");
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.action("unknownAction", "dataLink", "image1");
			}
		});
		assertEquals("Map doesn't have key unknownAction. Legal keys are [action1]. Map is {action1=ActionMock [name=1]}", e.getMessage());
		assertEquals(Arrays.asList(new ImageButtonDefn("image1", null, "dataLink", Maps.<SmallIconPosition, String> newMap(), action)), displayerDefn.defns);
	}

	public void testDataThrowsExceptionIfCalledTwice() {
		displayerDefn.data("dataKey1");
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.data("dataKey2");

			}
		});
		assertEquals("Cannot set value of data twice. Current value [dataKey1]. New value [dataKey2]", e.getMessage());
		assertEquals("dataKey1", displayerDefn.dataKey);
	}

	public void testTooltipThrowsExceptionIfCalledTwice() {
		displayerDefn.tooltip("tooltip1");
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.tooltip("tooltip2");

			}
		});
		assertEquals("Cannot set value of tooltip twice. Current value [tooltip1]. New value [tooltip2]", e.getMessage());
		assertEquals("tooltip1", displayerDefn.tooltip);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		displayer = new DisplayerMock("1");
		action = new ActionMock("1");
		displayerDefn = new DisplayerDefn(displayer, new ActionStore().action("action1", action));
	}

}
