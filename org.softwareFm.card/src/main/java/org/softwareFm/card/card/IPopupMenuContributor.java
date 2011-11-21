package org.softwareFm.card.card;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.card.card.internal.CardPopupMenuContributor;

/** the popup menu is about an item (typically an ICard). This adds items to the menu, prior to it being seen */
public interface IPopupMenuContributor<T> {

	void contributeTo(Event event, Menu menu, T relevantItem);

	public static class Utils {
		public static IPopupMenuContributor<ICard> cardPopupMenuContributor() {
			return new CardPopupMenuContributor();
		}

		public static <T> IPopupMenuContributor<T> noContributor() {
			return new IPopupMenuContributor<T>() {
				@Override
				public void contributeTo(Event event, Menu menu, T relevantItem) {
				}
			};
		}

		public static <T> IPopupMenuContributor<T> sysout() {
			return new IPopupMenuContributor<T>() {
				@Override
				public void contributeTo(Event event, Menu menu, T relevantItem) {
					System.out.println("Contributing to menu " + menu + " for  " + relevantItem);
				}
			};
		}
	}
}
