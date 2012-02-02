/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.menu.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.menu.IPopupMenuContributor;
import org.softwareFm.swt.menu.IPopupMenuService;

public class PopupMenuService<T> implements IPopupMenuService<T> {

	private final Set<String> menuIds = Sets.newSet();
	private final Map<String, List<IPopupMenuContributor<T>>> map = Maps.newMap();

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
	public void contributeTo(String popupMenuId, Event event, Menu menu, T relevantItem) {
		if (popupMenuId == null)
			return;
		checkMenuId(popupMenuId);
		List<IPopupMenuContributor<T>> list = map.get(popupMenuId);
		for (IPopupMenuContributor<T> contributor : Lists.nullSafe(list))
			contributor.contributeTo(event, menu, relevantItem);
	}

	private void checkMenuId(String menuId) {
		if (!menuIds.contains(menuId))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.illegalMenuId, menuId, Iterables.list(menuIds)));
	}

}