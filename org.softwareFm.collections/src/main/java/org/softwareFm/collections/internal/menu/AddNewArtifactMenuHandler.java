/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.utilities.resources.IResourceGetter;

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