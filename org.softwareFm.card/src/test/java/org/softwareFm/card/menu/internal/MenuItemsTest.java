package org.softwareFm.card.menu.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.menu.IMenuItems;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class MenuItemsTest extends SwtIntegrationTest {

	private Menu menu;
	private ICard card;

	private MenuItemHandlerMock mock0;
	private MenuItemHandlerMock mock1;
	private MenuItemHandlerMock mock_null_0;
	private MenuItemHandlerMock mock_null_1;

	public void testContributeCallsOptionalInOrderAndAddsAllNonNull() {
		IMenuItems menuItems = IMenuItems.Utils.menuItems("zero", mock0, "one", mock1, "null_0", mock_null_0, "null_1", mock_null_1);
		menuItems.contributeTo(makeEvent(), menu, card);
		assertEquals(2, menu.getItemCount());
		MenuItem item0 = menu.getItem(0);
		MenuItem item1 = menu.getItem(1);

		assertEquals(Lists.getOnly(mock0.createdItems), item0);
		assertEquals(Lists.getOnly(mock1.createdItems), item1);
	}

	public void testMenuItemSelectListenerCallsMenuHandlerExecuteMethod() {
		checkSelectListenerCallsExecute(0, mock0);
		checkSelectListenerCallsExecute(1, mock1);
	}
	
	public void testFindKeyIsBasedOnEventXY(){
		int keyIndex = 0;
		IMenuItems menuItems = IMenuItems.Utils.menuItems("zero", mock0, "one", mock1, "null_0", mock_null_0, "null_1", mock_null_1);
		card.getControl().setSize(100, 100); 
		Table table = card.getTable();
		TableItem tableItem = table.getItem(keyIndex);
		Rectangle bounds = tableItem.getBounds();
		Event event = new Event();
		event.widget = table;
		Point p = new Point(bounds.x+1, bounds.y+1);
		Point pInDisplay = table.toDisplay(p);
		event.x = pInDisplay.x;
		event.y = pInDisplay.y;
		menuItems.contributeTo(event, menu, card);
		
		assertEquals(tableItem.getData(), Lists.getOnly(mock0.keys));
		assertEquals(tableItem.getData(), Lists.getOnly(mock1.keys));
	}

	private void checkSelectListenerCallsExecute(int index, MenuItemHandlerMock mock) {
		IMenuItems menuItems = IMenuItems.Utils.menuItems("one", mock0, "two", mock1);
		menuItems.contributeTo(makeEvent(), menu, card);
		MenuItem item = menu.getItem(index);
		assertEquals(0, mock.executeCards.size());
		Event selectionEvent = new Event();
		item.notifyListeners(SWT.Selection, selectionEvent);
		assertEquals(card, Lists.getOnly(mock.executeCards));
		assertEquals(item, Lists.getOnly(mock.executeItems));
	}

	private Event makeEvent() {
		Event event = new Event();
		event.widget = card.getTable();
		return event;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		card = ICardFactory.Utils.createCardWithLayout(shell, CardDataStoreFixture.syncCardConfig(display), "someUrl", Maps.stringObjectMap("a", 1, "b", 2));

		menu = new Menu(shell, SWT.NULL);

		mock0 = new MenuItemHandlerMock("0", true);
		mock1 = new MenuItemHandlerMock("1", true);
		mock_null_0 = new MenuItemHandlerMock("null_0", false);
		mock_null_1 = new MenuItemHandlerMock("null_1", false);

	}

}
