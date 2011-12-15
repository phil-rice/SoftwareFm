/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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