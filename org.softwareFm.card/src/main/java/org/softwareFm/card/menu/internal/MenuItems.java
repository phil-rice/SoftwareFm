/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.menu.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.softwareFm.utilities.collections.Lists;

public class MenuItems implements IMenuItems {

	private final List<ICardMenuItemHandler> handlers;

	public MenuItems(ICardMenuItemHandler[] handlers) {
		this(Arrays.asList(handlers));
	}

	private MenuItems(List<ICardMenuItemHandler> handlers) {
		this.handlers = new ArrayList<ICardMenuItemHandler>(handlers);
	}

	@Override
	public IMenuItems with(ICardMenuItemHandler... handler) {
		return new MenuItems(Lists.append(handlers, handler));
	}

	@Override
	public void contributeTo(Event event, Menu menu, final ICard card) {
		String key = findKey(event);
		for (final ICardMenuItemHandler menuItemHandler : handlers) {
			final MenuItem menuItem = menuItemHandler.optionallyCreate(card, CardConfig.resourceGetter(card), menu, event, key);
			if (menuItem != null)
				menuItem.addListener(SWT.Selection, new Listener() {
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