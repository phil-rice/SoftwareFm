/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.menu.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.menu.AbstractCardMenuHandler;

public class ViewCardsMenuHandler extends AbstractCardMenuHandler {

	public ViewCardsMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		Object value = card.data().get(key);
		if (value instanceof Map<?, ?>) {
			String viewActualPattern = resourceGetter.getStringOrNull(CardConstants.menuItemViewCards);
			if (viewActualPattern != null) {
				MenuItem menuItem = new MenuItem(menu, SWT.NULL);
				menuItem.setText(MessageFormat.format(viewActualPattern, key));
				return menuItem;
			}
		}
		return null;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		explorer.showContents();
	}

}