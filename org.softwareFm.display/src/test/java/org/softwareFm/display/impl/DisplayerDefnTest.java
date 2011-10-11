package org.softwareFm.display.impl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.DisplayerMock;
import org.softwareFm.utilities.tests.Tests;

public class DisplayerDefnTest extends TestCase {

	private DisplayerMock displayer;
	private DisplayerDefn displayerDefn;

	ActionDefn actionDefn1 = new ActionDefn("action1", "mainImageLink1", "overlayId1");
	ActionDefn actionDefn2 = new ActionDefn("action2", "mainImageLink2", "overlayId2");
	ActionDefn actionDefnDefault1 = new ActionDefn("actionDefault1", "mainImageLinkDefault1", "overlayIdDefault1").thisIsDefault();
	ActionDefn actionDefnDefault2 = new ActionDefn("actionDefault2", "mainImageLinkDefault2", "overlayIdDefault2").thisIsDefault();
	ActionDefn actionDefnUnknown = new ActionDefn("actionU", "mainImageLinkU", "ovelayIdU");

	public void testConstructor() {
		assertEquals(displayer, displayerDefn.displayerFactory);
		assertEquals(null, displayerDefn.dataKey);
		assertEquals(null, displayerDefn.tooltip);
		assertEquals(null, displayerDefn.iconImageId);
		assertEquals(null, displayerDefn.iconOverlayId);
		assertEquals(false, displayerDefn.noIcon);
	}
	
	public void testIcon(){
		assertSame(displayerDefn, displayerDefn.icon("iconId"));
		assertEquals("iconId", displayerDefn.iconImageId);
		assertEquals(null, displayerDefn.iconOverlayId);
	}
	public void testIconWithOverlay(){
		assertSame(displayerDefn, displayerDefn.icon("iconId", "overlayId"));
		assertEquals("iconId", displayerDefn.iconImageId);
		assertEquals("overlayId", displayerDefn.iconOverlayId);
	}

	
	public void testNoIcon(){
		assertSame(displayerDefn, displayerDefn.noIcon());
		assertTrue(displayerDefn.noIcon);
	}
	public void testData() {
		assertSame(displayerDefn, displayerDefn.data("dataKey"));
		assertEquals("dataKey", displayerDefn.dataKey);
	}

	public void testTooltip() {
		assertSame(displayerDefn, displayerDefn.tooltip("tooltip"));
		assertEquals("tooltip", displayerDefn.tooltip);
	}
	
	public void testEditor(){
		assertSame(displayerDefn, displayerDefn.editor("editorId"));
		assertEquals("editorId", displayerDefn.editorId);
		
	}

	public void testButton() {
		assertSame(displayerDefn, displayerDefn.actions(actionDefn1, actionDefn2));
		assertEquals(Arrays.asList(actionDefn1, actionDefn2), displayerDefn.actionDefns);
	}

	public void testDefaultActionIsNullWhenNoActions() {
		assertNull( displayerDefn.getDefaultActionDefn());
		
	}
	public void testDefaultActionIsNormallyFirstAction() {
		 displayerDefn.actions(actionDefn1, actionDefn2);
		 assertEquals(actionDefn1, displayerDefn.getDefaultActionDefn());
	}

	public void testActionWithDefaultActionOverridesFirst() {
		displayerDefn.actions(actionDefn1, actionDefn2, actionDefnDefault1);
		assertEquals(actionDefnDefault1, displayerDefn.getDefaultActionDefn());

	}

	public void testCannotHaveTwoDefaultActions() {
		IllegalStateException exception = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.actions(actionDefn1, actionDefn2, actionDefnDefault1, actionDefnDefault2);
			}
		});
		assertEquals("Cannot have two default actions in displayer defn for data null.\nExisting valueActionDefn [id=actionDefault1, mainImageId=mainImageLinkDefault1, tooltip=null, overlayId=overlayIdDefault1, ignoreGuard=false, defaultAction=true, params=null]\nNew valueActionDefn [id=actionDefault2, mainImageId=mainImageLinkDefault2, tooltip=null, overlayId=overlayIdDefault2, ignoreGuard=false, defaultAction=true, params=null]", exception.getMessage());
	}
	
	public void testCannotSetActionsTwice(){
		displayerDefn.actions(actionDefn1, actionDefn2);
		IllegalStateException exception = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.actions(actionDefn1, actionDefn2);
			}
		});
		assertEquals("Cannot define actions twice in displayer defn for data null.\nExisting value[ActionDefn [id=action1, mainImageId=mainImageLink1, tooltip=null, overlayId=overlayId1, ignoreGuard=false, defaultAction=false, params=null], ActionDefn [id=action2, mainImageId=mainImageLink2, tooltip=null, overlayId=overlayId2, ignoreGuard=false, defaultAction=false, params=null]]\nNew value[ActionDefn [id=action1, mainImageId=mainImageLink1, tooltip=null, overlayId=overlayId1, ignoreGuard=false, defaultAction=false, params=null], ActionDefn [id=action2, mainImageId=mainImageLink2, tooltip=null, overlayId=overlayId2, ignoreGuard=false, defaultAction=false, params=null]]", exception.getMessage());
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

	
	public void testEditorThrowsExceptionIfCalledTwice() {
		displayerDefn.editor("id1");
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.editor("id1");
			}
		});
		assertEquals("Cannot define editor twice in displayer defn for data null.\nExisting valueid1\nNew valueid1", e.getMessage());
		assertEquals("id1", displayerDefn.editorId);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		displayer = new DisplayerMock("1");
		displayerDefn = new DisplayerDefn(displayer);
	}

}
