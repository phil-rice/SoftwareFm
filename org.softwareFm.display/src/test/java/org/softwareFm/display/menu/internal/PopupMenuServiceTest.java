package org.softwareFm.display.menu.internal;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.junit.Test;
import org.softwareFm.display.menu.IPopupMenuService;
import org.softwareFm.display.menu.PopupMenuContributorMock;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.tests.Tests;

public class PopupMenuServiceTest extends SwtIntegrationTest {

	private PopupMenuContributorMock<String> mock1_1;
	private PopupMenuContributorMock<String> mock1_2;
	private PopupMenuContributorMock<String> mock2_1;
	private IPopupMenuService<String> popUpMenuService;
	private Menu menu;
	private Event event;

	@Test
	public void testContributorsWithCorrectIdGetCalled() {
		popUpMenuService.contributeTo("one", event, menu, "relevantItem");
		checkCalledOnce(mock1_1);
		checkCalledOnce(mock1_2);
		checkNotCalled(mock2_1);

		popUpMenuService.contributeTo("two", event, menu, "relevantItem");
		checkCalledOnce(mock1_1);
		checkCalledOnce(mock1_2);
		checkCalledOnce(mock2_1);
	}
	
	public void testOrderPreserved(){
		popUpMenuService.contributeTo("one", event, menu, "relevantItem");
		assertEquals(0, Lists.getOnly(mock1_1.counts).intValue());
		assertEquals(1, Lists.getOnly(mock1_2.counts).intValue());
	}
	public void testOrderPreservedWithReverse(){
		popUpMenuService.contributeTo("reverseOrder", event, menu, "relevantItem");
		assertEquals(1, Lists.getOnly(mock1_1.counts).intValue());
		assertEquals(0, Lists.getOnly(mock1_2.counts).intValue());
	}
	
	public void testContributeWithNullIsIgnored(){
		popUpMenuService.contributeTo(null, event, menu, "relevantItem");
		checkNotCalled(mock1_1);
		checkNotCalled(mock1_2);
		checkNotCalled(mock2_1);
		
	}
	public void testCannotRegisterContributorAgainstUnknownId(){
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				popUpMenuService.registerContributor("notIn", new PopupMenuContributorMock<String>());
			}
		});
		assertEquals("Illegal Menu Id notIn. Legal values are [reverseOrder, two, one]", e.getMessage());
	}
	
	public void testCannotContributeToUnknownMenu(){
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				popUpMenuService.contributeTo("notIn", event, menu, "anything");
			}
		});
		assertEquals("Illegal Menu Id notIn. Legal values are [reverseOrder, two, one]", e.getMessage());
		
	}

	private void checkCalledOnce(PopupMenuContributorMock<String> mock) {
		assertEquals(event, Lists.getOnly(mock.events));
		assertEquals(menu, Lists.getOnly(mock.menus));
		assertEquals("relevantItem", Lists.getOnly(mock.relevantItems));
	}
	private void checkNotCalled(PopupMenuContributorMock<String> mock) {
		assertEquals(0, mock.events.size());
		assertEquals(0, mock.menus.size());
		assertEquals(0, mock.relevantItems.size());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		PopupMenuContributorMock.reset();
		mock1_1 = new PopupMenuContributorMock<String>();
		mock1_2 = new PopupMenuContributorMock<String>();
		mock2_1 = new PopupMenuContributorMock<String>();
		popUpMenuService = IPopupMenuService.Utils.popUpMenuService();
		popUpMenuService.registerMenuId("one");
		popUpMenuService.registerMenuId("two");
		popUpMenuService.registerMenuId("reverseOrder");
		popUpMenuService.registerContributor("one", mock1_1);
		popUpMenuService.registerContributor("one", mock1_2);
		popUpMenuService.registerContributor("two", mock2_1);
		popUpMenuService.registerContributor("reverseOrder", mock1_2);
		popUpMenuService.registerContributor("reverseOrder", mock1_1);
		menu = new Menu(shell);
		event = new Event();
	}

}
