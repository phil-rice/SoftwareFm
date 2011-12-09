package org.softwareFm.collections.menu.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.AbstractCardMenuHandler;
import org.softwareFm.utilities.resources.IResourceGetter;

public class EditSnippetMenuHandler extends AbstractCardMenuHandler {

	public EditSnippetMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		MenuItem menuItem = new MenuItem(menu, SWT.NULL);
		menuItem.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemEditSnippetText));
		return menuItem;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		explorer.editSnippet(card, key);
	}

}