/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.explorer.IExplorer;

public abstract class AbstractCardMenuHandler implements ICardMenuItemHandler {

	protected final IExplorer explorer;

	public AbstractCardMenuHandler(IExplorer explorer) {
		this.explorer = explorer;
	}

	public AbstractCardMenuHandler() {
		this(null);
	}

	@Override
	public void contributeTo(Event event, Menu menu, final ICard card) {
		int selectionIndex = card.getTable().getSelectionIndex();
		TableItem item = selectionIndex == -1 ? null : card.getTable().getItem(selectionIndex);
		IResourceGetter resourceGetter = Functions.call(card.getCardConfig().resourceGetterFn, card.cardType());
		final String key = item == null ? null : (String) item.getData();
		final MenuItem menuItem = optionallyCreate(card, resourceGetter, menu, event, key);
		if (menuItem != null)
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					execute(card, key, menuItem);
				}
			});
	}

}