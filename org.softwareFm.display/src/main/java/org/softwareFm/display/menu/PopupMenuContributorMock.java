package org.softwareFm.display.menu;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.utilities.collections.Lists;

public class PopupMenuContributorMock <T>implements IPopupMenuContributor<T> {

	public final static AtomicInteger count = new AtomicInteger();
	public static void reset(){
		count.set(0);
	}
	public List<Event> events = Lists.newList();
	public List<Menu> menus = Lists.newList();
	public List<T> relevantItems = Lists.newList();
	public List<Integer>counts = Lists.newList();

	@Override
	public void contributeTo(Event event, Menu menu, T relevantItem) {
		events.add(event);
		menus.add(menu);
		relevantItems.add(relevantItem);
		counts.add(count.getAndIncrement());
	}

}
