/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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

public class HelpMenuHandler extends AbstractCardMenuHandler {
	public HelpMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		String helpKey = "help." + card.cardType() + "." + key;
		String helpText = IResourceGetter.Utils.getOrNull(card.getCardConfig().resourceGetterFn, card.cardType(), helpKey);
		if (helpText!=null){
			String helpMenuItemText = IResourceGetter.Utils.getOrException(card.getCardConfig().resourceGetterFn, card.cardType(), CardConstants.menuItemHelpText);
			MenuItem menuItem = new MenuItem(menu, SWT.NULL);
			menuItem.setText(helpMenuItemText);
			menuItem.setData(helpText);
			return menuItem;
		}
		return null;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		explorer.displayHelpText(card.cardType(), (String) item.getData());
	}

}