/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.collections.internal.menu;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.collections.explorer.IExplorer;

public class ViewContentsMenuHandler implements ICardMenuItemHandler {

	private final IExplorer explorer;

	public ViewContentsMenuHandler(IExplorer explorer) {
		this.explorer = explorer;
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		String cardContentFieldName = IResourceGetter.Utils.getOrNull(card.cardConfig().resourceGetterFn, key, CardConstants.cardContentField);
		if (cardContentFieldName != null) {
			String viewActualPattern = resourceGetter.getStringOrNull(CardConstants.menuItemViewActual);
			if (viewActualPattern != null) {
				MenuItem viewContents = new MenuItem(menu, SWT.NULL);
				viewContents.setText(MessageFormat.format(viewActualPattern, key));
				return viewContents;
			}
		}
		return null;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		explorer.showContents();
	}

}