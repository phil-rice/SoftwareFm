package org.softwarefm.collections.internal.menu;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.resources.IResourceGetter;

public class AddItemToCollectionMenuHandler implements ICardMenuItemHandler {

	@Override
	public MenuItem optionallyCreate(final ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		final RightClickCategoryResult categorisation = card.cardConfig().rightClickCategoriser.categorise(card.url(), card.data(), key);
		switch (categorisation.itemType) {
		case IS_COLLECTION:
		case ROOT_COLLECTION:
			MenuItem menuItem = new MenuItem(menu, SWT.NULL);
			String addCollectionPattern = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemAddCollection);
			menuItem.setText(MessageFormat.format(addCollectionPattern, categorisation.collectionName));
			menuItem.setData(categorisation);
			return menuItem;
		default:
		}
		return null;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		CardConfig cardConfig = card.cardConfig();
		RightClickCategoryResult categorisation = (RightClickCategoryResult) item.getData();
		cardConfig.addItemProcessor.addCollectionItem(categorisation);
	}
}
