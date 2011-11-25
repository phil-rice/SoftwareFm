package org.softwarefm.collections.internal.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.collections.explorer.IExplorer;

public class AddNewArtifactMenuHandler implements ICardMenuItemHandler {

	private final IExplorer explorer;
	
	public AddNewArtifactMenuHandler(IExplorer explorer) {
		this.explorer = explorer;
	}

	@Override
	public MenuItem optionallyCreate(final ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		MenuItem menuItem = new MenuItem(menu, SWT.NULL);
		menuItem.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemAddArtifact));
		return menuItem;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		explorer.showAddNewArtifactEditor();
	}

}
