package org.softwareFm.display.impl;

import java.util.Collections;

import junit.framework.TestCase;

import org.softwareFm.display.actions.ActionMock;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.display.smallButtons.SmallButtonFactoryMock;
import org.softwareFm.utilities.tests.Tests;

public class SmallButtonDefnTest extends TestCase {

	private SmallButtonDefn defn;
	private ActionMock action1;
	private SmallButtonFactoryMock smallButtonFactory;

	public void testSmallButtonDefn() {
		assertEquals("someId", defn.id);
		assertEquals(null, defn.tooltip);
		assertEquals("titleId", defn.titleId);
		assertEquals(smallButtonFactory, defn.smallButtonFactory);
		assertEquals(Collections.EMPTY_LIST, defn.defns);
		assertEquals("mainImageId", defn.mainImageId);
	}

	public void testAddingToolip() {
		assertSame(defn, defn.tooltip("someTooltip"));
		assertEquals("someTooltip", defn.tooltip);
	}

	public void testAddingDataId() {
		assertSame(defn, defn.data("someData"));
		assertEquals("someData", defn.dataId);

	}

	public void testCannotAddSecondTooltip() {
		defn.tooltip("someTooltip");
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				defn.tooltip("someOtherTooltip");

			}
		});
		assertEquals("Cannot set value of tooltip twice. Current value [someTooltip]. New value [someOtherTooltip]", e.getMessage());
		assertEquals("someTooltip", defn.tooltip);
	}

	public void testCannotAddSecondData() {
		defn.data("someData");
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				defn.data("someData");
			}
		});
		assertEquals("Cannot set value of tooltip twice. Current value [someTooltip]. New value [someOtherTooltip]", e.getMessage());
		assertEquals("someTooltip", defn.tooltip);
	}

	public void testControlClickAction() {
		assertSame(defn, defn.ctrlClickAction("action1", "data"));
		assertEquals(action1, defn.controlClickAction);
		assertEquals("data", defn.controlClickActionData);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		action1 = new ActionMock("1");
		ActionStore store = new ActionStore().//
				action("action1", action1);
		smallButtonFactory = new SmallButtonFactoryMock("1");
		defn = new SmallButtonDefn("someId", "titleId", "mainImageId", store, smallButtonFactory);
	}
}
