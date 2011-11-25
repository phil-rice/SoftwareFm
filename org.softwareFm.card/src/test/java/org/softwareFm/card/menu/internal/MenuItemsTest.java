package org.softwareFm.card.menu.internal;

import java.util.Arrays;

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
		IMenuItems menuItems = IMenuItems.Utils.menuItems(mock0, mock1, mock_null_0, mock_null_1);
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

	public void testWithDoesntTrashOriginal() {
		IMenuItems menuItems0 = IMenuItems.Utils.menuItems(mock0);
		IMenuItems menuItems1 = menuItems0.with(mock1);

		menuItems0.contributeTo(makeEvent(), menu, card);
		assertEquals(1, menu.getItemCount());
		MenuItem item0a = menu.getItem(0);
		Menu second = new Menu(shell, SWT.NULL);
		menuItems1.contributeTo(makeEvent(),second, card);
		assertEquals(2, second.getItemCount());

		MenuItem item0b = second.getItem(0);
		MenuItem item1 = second.getItem(1);
		assertEquals(Arrays.asList(item0a, item0b), mock0.createdItems);
		assertEquals(Arrays.asList(item1), mock1.createdItems);
	}

	public void testFindKeyIsBasedOnEventXY() {
		int keyIndex = 0;
		card.getControl().setSize(100, 100);
		Table table = card.getTable();
		TableItem tableItem = table.getItem(keyIndex);
		Rectangle bounds = tableItem.getBounds();
		Event event = new Event();
		event.widget = table;
		Point p = new Point(bounds.x + 1, bounds.y + 1);
		Point pInDisplay = table.toDisplay(p);
		event.x = pInDisplay.x;
		event.y = pInDisplay.y;
		IMenuItems menuItems = IMenuItems.Utils.menuItems(mock0, mock1, mock_null_0, mock_null_1);
		menuItems.contributeTo(event, menu, card);

		assertEquals(tableItem.getData(), Lists.getOnly(mock0.keys));
		assertEquals(tableItem.getData(), Lists.getOnly(mock1.keys));
	}

	private void checkSelectListenerCallsExecute(int index, MenuItemHandlerMock mock) {
		IMenuItems menuItems = IMenuItems.Utils.menuItems(mock0, mock1);
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
