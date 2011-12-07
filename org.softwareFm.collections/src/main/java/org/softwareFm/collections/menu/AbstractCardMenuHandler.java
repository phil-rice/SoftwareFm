package org.softwareFm.collections.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

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
