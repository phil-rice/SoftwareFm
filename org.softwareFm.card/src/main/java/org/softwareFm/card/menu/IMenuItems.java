package org.softwareFm.card.menu;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.IPopupMenuContributor;
import org.softwareFm.card.menu.internal.MenuItems;
import org.softwareFm.utilities.maps.Maps;

public interface IMenuItems extends IPopupMenuContributor<ICard>{


	public static class Utils {
		public static IMenuItems menuItems(Object... nameAndHandlers) {
			return new MenuItems(Maps.<String, ICardMenuItemHandler> makeLinkedMap(nameAndHandlers));
		}
	}
}
