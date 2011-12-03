package org.softwareFm.display.menu;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.display.menu.internal.PopupMenuService;

public interface IPopupMenuService<T> {

	public void registerMenuId(String menuId);

	public void registerContributor(String menuId, IPopupMenuContributor<T> contributor);

	void contributeTo(String menuId, Event event, Menu menu, T relevantItem);
	
	public static class Utils {
		public static <T>IPopupMenuService <T>popUpMenuService(){
			return new PopupMenuService<T>();
			
		}
	}
}
