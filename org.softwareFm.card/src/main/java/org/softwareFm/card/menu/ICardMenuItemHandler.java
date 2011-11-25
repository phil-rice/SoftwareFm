package org.softwareFm.card.menu;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ICardMenuItemHandler {

	MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key);
	
	public void execute(ICard card, MenuItem item);
	
	
}
