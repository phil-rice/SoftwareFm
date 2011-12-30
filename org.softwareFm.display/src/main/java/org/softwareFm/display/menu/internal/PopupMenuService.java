/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.menu.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.menu.IPopupMenuContributor;
import org.softwareFm.display.menu.IPopupMenuService;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class PopupMenuService<T> implements IPopupMenuService<T> {

	private final Set<String> menuIds = Sets.newSet();
	private final Map<String, List<IPopupMenuContributor<T>>> map = Maps.newMap();
	private final IFunction1<T, String> menuIdFn;

	public PopupMenuService(IFunction1<T, String> menuIdFn) {
		this.menuIdFn = menuIdFn;
	}

	@Override
	public void registerMenuId(String menuId) {
		menuIds.add(menuId);
	}

	@Override
	public void registerContributor(String menuId, IPopupMenuContributor<T> contributor) {
		checkMenuId(menuId);
		Maps.addToList(map, menuId, contributor);
	}

	@Override
	public void contributeTo(Event event, Menu menu, T relevantItem) {
		String menuId = Functions.call(menuIdFn, relevantItem);
		if (menuId == null)
			return;
		checkMenuId(menuId);
		List<IPopupMenuContributor<T>> list = map.get(menuId);
		for (IPopupMenuContributor<T> contributor : Lists.nullSafe(list))
			contributor.contributeTo(event, menu, relevantItem);
	}

	private void checkMenuId(String menuId) {
		if (!menuIds.contains(menuId))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.illegalMenuId, menuId, Iterables.list(menuIds)));
	}

}