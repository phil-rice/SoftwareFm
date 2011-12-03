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
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.maps.Maps;

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
	public void contributeTo(String menuId, Event event, Menu menu, T relevantItem) {
		checkMenuId(menuId);
		List<IPopupMenuContributor<T>> list = map.get(menuId);
		for (IPopupMenuContributor<T> contributor : list)
			contributor.contributeTo(event, menu, relevantItem);
	}

	private void checkMenuId(String menuId) {
		if (!menuIds.contains(menuId))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.illegalMenuId, menuId, Iterables.list(menuIds)));
	}

}
