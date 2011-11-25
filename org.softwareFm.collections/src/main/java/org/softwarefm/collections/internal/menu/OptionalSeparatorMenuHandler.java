package org.softwarefm.collections.internal.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.resources.IResourceGetter;

public class OptionalSeparatorMenuHandler implements ICardMenuItemHandler {

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		if (menu.getItemCount() > 0)
			return new MenuItem(menu, SWT.SEPARATOR);
		else
			return null;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
	}

}
