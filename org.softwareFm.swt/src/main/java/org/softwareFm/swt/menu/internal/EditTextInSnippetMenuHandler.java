/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.menu.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.menu.AbstractCardMenuHandler;

public class EditTextInSnippetMenuHandler extends AbstractCardMenuHandler {

	public EditTextInSnippetMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		Object value = card.data().get(key);
		if (CardConstants.snippet.equals(card.cardType()) && value instanceof String) {
			String editor = resourceGetter.getStringOrNull("editor." + key);
			if (!"none".equals(editor)) {
				MenuItem menuItem = new MenuItem(menu, SWT.NULL);
				menuItem.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemEditText));
				return menuItem;
			}
		}
		return null;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		explorer.displayHelpTextFor(card, key);
		explorer.edit(card, key);
	}

}