package org.softwareFm.card.card;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;

/** the popup menu is about an item (typically an ICard). This adds items to the menu, prior to it being seen */
public interface IPopupMenuContributor<T> {

	void contributeTo(Event event, Menu menu, T relevantItem);

	public static class Utils {

		public static IPopupMenuContributor<ICard> noContributor() {
			return new IPopupMenuContributor<ICard>() {
				@Override
				public void contributeTo(Event event, Menu menu, ICard relevantItem) {
				}
			};
		}

		public static IPopupMenuContributor<ICard> sysout() {
			return new IPopupMenuContributor<ICard>() {
				@Override
				public void contributeTo(Event event, Menu menu, ICard relevantItem) {
					System.out.println("Contributing to menu " + menu + " for  " + relevantItem);
				}
			};
		}
	}
}
