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
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Urls;

public class BrowseUrlMenuHandler extends AbstractCardMenuHandler {

	public BrowseUrlMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		Object value = card.data().get(key);
		if (value instanceof String) {
			String string = (String) value;
			if (Urls.isUrl(string)) {
				MenuItem item = new MenuItem(menu, SWT.NULL);
				item.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemBrowseText));
				return item;
			}
		}

		return null;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		String url = (String) card.data().get(key);
		explorer.processUrl(DisplayConstants.browserFeedType, url.trim());

	}

}