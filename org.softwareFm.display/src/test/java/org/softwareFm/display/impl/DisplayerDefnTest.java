package org.softwareFm.display.impl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.display.ActionDefn;
import org.softwareFm.display.displayer.DisplayerMock;
import org.softwareFm.utilities.tests.Tests;

public class DisplayerDefnTest extends TestCase {

	private DisplayerMock displayer;
	private DisplayerDefn displayerDefn;

	ActionDefn actionDefn1 = new ActionDefn("action1", "mainImageLink1", "ovelayId1");
	ActionDefn actionDefn2 = new ActionDefn("action2", "mainImageLink2", "ovelayId2");
	ActionDefn actionDefnUnknown = new ActionDefn("actionU", "mainImageLinkU", "ovelayIdU");

	public void testConstructor() {
		assertEquals(displayer, displayerDefn.displayerFactory);
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
		assertSame(displayerDefn, displayerDefn.actions(actionDefn1, actionDefn2));
		assertEquals(Arrays.asList(actionDefn1, actionDefn2), displayerDefn.actionDefns);
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
		displayerDefn = new DisplayerDefn(displayer);
	}

}