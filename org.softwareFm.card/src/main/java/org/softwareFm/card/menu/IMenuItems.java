package org.softwareFm.card.menu;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.IPopupMenuContributor;
import org.softwareFm.card.menu.internal.MenuItems;

public interface IMenuItems extends IPopupMenuContributor<ICard> {

	public IMenuItems with(ICardMenuItemHandler ...handler);

	public static class Utils {
		public static IMenuItems menuItems(ICardMenuItemHandler... handlers) {
			return new MenuItems(handlers);
		}
	}
}
