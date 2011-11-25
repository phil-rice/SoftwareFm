package org.softwareFm.card.menu.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.card.menu.IMenuItems;

public class MenuItems implements IMenuItems {

	private final Map<String, ICardMenuItemHandler> map ;

	public MenuItems(Map<String, ICardMenuItemHandler> map) {
		this.map = new LinkedHashMap<String, ICardMenuItemHandler>(map);
	}


	@Override
	public void contributeTo(Event event, Menu menu, final ICard card) {
		String key = findKey(event);
		for (Entry<String, ICardMenuItemHandler> entry: map.entrySet()){
			final ICardMenuItemHandler menuItemHandler = entry.getValue();
			final MenuItem menuItem = menuItemHandler.optionallyCreate(card, CardConfig.resourceGetter(card), menu, event, key);
			if (menuItem!= null)
				menuItem.addListener(SWT.Selection , new Listener() {
					@Override
					public void handleEvent(Event event) {
						menuItemHandler.execute(card, menuItem);
					}
				});
		}
		
	}

	protected String findKey(Event event) {
		Table table = (Table) event.widget;
		Point location = new Point(event.x, event.y);
		Point inMySpace = table.toControl(location);
		TableItem item = table.getItem(inMySpace);
		String key = (String) (item == null ? null : item.getData());
		return key;
	}

}
