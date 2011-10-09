package org.softwareFm.display;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.utilities.tests.Tests;

public class ActionDefnTest extends TestCase {

	public void testConstructor() {
		ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId");
		assertEquals("id", actionDefn.id);
		assertEquals("mainImageId", actionDefn.mainImageId);
		assertEquals("overlayId", actionDefn.overlayId);
		assertEquals(null, actionDefn.tooltip);
		assertEquals(null, actionDefn.params);
		assertFalse(actionDefn.defaultAction);
	}

	public void testTooltip() {
		ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId");
		assertSame(actionDefn, actionDefn.tooltip("tooltip"));
		assertEquals("tooltip", actionDefn.tooltip);
	}

	public void testParams() {
		ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId");
		assertSame(actionDefn, actionDefn.params("p1", "p2"));
		assertEquals(Arrays.asList("p1", "p2"), actionDefn.params);
	}

	public void testDefaultAction() {
		ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId");
		assertFalse(actionDefn.defaultAction);
		assertSame(actionDefn, actionDefn.thisIsDefault());
		assertTrue(actionDefn.defaultAction);
	}

	public void testCannotUseTooltipTwice() {
		final ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId").tooltip("t");
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				actionDefn.tooltip("another");
			}
		});
		assertEquals("t", actionDefn.tooltip);
		assertEquals("Cannot set value of tooltip twice. Current value [t]. New value [another]", e.getMessage());
	}

	public void testCannotUseParamsTwice() {
		final ActionDefn actionDefn = new ActionDefn("id", "mainImageId", "overlayId").params("p1");
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				actionDefn.params("p2");
			}
		});
		assertEquals(Arrays.asList("p1"), actionDefn.params);
		assertEquals("Cannot set value of params twice. Current value [[p1]]. New value [[p2]]", e.getMessage());

	}

	public void testConstructorWithNullArguments() {
		check(null, "mainImageId", "overlayId", "Must have a id in null");
		check("id", null, "overlayId", "Must have a mainImageId in id");
		new ActionDefn("id", "main", null);
	}

	private void check(final String id, final String mainImageId, final String overlayId, String message) {
		NullPointerException e = Tests.assertThrows(NullPointerException.class, new Runnable() {
			@Override
			public void run() {
				new ActionDefn(id, mainImageId, overlayId);
			}
		});
		assertEquals(message, e.getMessage());
	}

}
