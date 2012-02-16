/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.menu;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.common.collections.Lists;

public class PopupMenuContributorMock<T> implements IPopupMenuContributor<T> {

	public final static AtomicInteger count = new AtomicInteger();

	public static void reset() {
		count.set(0);
	}

	public List<Event> events = Lists.newList();
	public List<Menu> menus = Lists.newList();
	public List<T> relevantItems = Lists.newList();
	public List<Integer> counts = Lists.newList();

	@Override
	public void contributeTo(Event event, Menu menu, T relevantItem) {
		events.add(event);
		menus.add(menu);
		relevantItems.add(relevantItem);
		counts.add(count.getAndIncrement());
	}

}