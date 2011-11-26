/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.menu.internal;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.junit.Assert;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

public class MenuItemHandlerMock implements ICardMenuItemHandler {

	private final String id;
	public final List<MenuItem> executeItems = Lists.newList();
	public final List<ICard> executeCards = Lists.newList();

	public final List<String> keys = Lists.newList();
	public final List<Event> events = Lists.newList();
	public final List<ICard> createCards = Lists.newList();
	private final boolean makeItem;
	public final List<MenuItem> createdItems = Lists.newList();

	public MenuItemHandlerMock(String id, boolean makeItem) {
		super();
		this.id = id;
		this.makeItem = makeItem;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		Assert.assertTrue(makeItem);
		executeCards.add(card);
		executeItems.add(item);
	}

	@Override
	public String toString() {
		return "MenuItemHandlerMock [id=" + id + "]";
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		Assert.assertSame(resourceGetter, Functions.call(card.cardConfig().resourceGetterFn, card.cardType()));
		keys.add(key);
		events.add(event);
		createCards.add(card);
		if (makeItem) {
			MenuItem menuItem = new MenuItem(menu, SWT.NULL);
			createdItems.add(menuItem);
			return menuItem;
		} else
			return null;
	}

}