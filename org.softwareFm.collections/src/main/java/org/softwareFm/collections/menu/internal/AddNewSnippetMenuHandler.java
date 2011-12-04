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

public class AddNewSnippetMenuHandler extends AbstractCardMenuHandler {
	
	
	public AddNewSnippetMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		String cardType = card.cardType();
		if (cardType == null || CardConstants.collection.equals(cardType)) {//Note that at the moment, there is no way to say "this is a collection of snippets... so this at least means that it will only add a snippet to a collection
			MenuItem menuItem = new MenuItem(menu, SWT.NULL);
			menuItem.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemAddSnippet));
			return menuItem;
		}
		return null;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		explorer.showAddSnippetEditor(card);
	}
}
