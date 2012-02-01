/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.menu.internal;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.AbstractCardMenuHandler;
import org.softwareFm.common.resources.IResourceGetter;

public class AddItemToCollectionMenuHandler extends AbstractCardMenuHandler {

	public AddItemToCollectionMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(final ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		IRightClickCategoriser categoriser = card.getCardConfig().rightClickCategoriser;
		String url = card.url();
		final RightClickCategoryResult categorisation = categoriser.categorise(url, card.data(), key);
		switch (categorisation.itemType) {
		case COLLECTION_NOT_CREATED_YET:
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
	public void execute(ICard card, String key, MenuItem item) {
		RightClickCategoryResult categorisation = (RightClickCategoryResult) item.getData();
		explorer.showAddCollectionItemEditor(card, categorisation);
	}
}