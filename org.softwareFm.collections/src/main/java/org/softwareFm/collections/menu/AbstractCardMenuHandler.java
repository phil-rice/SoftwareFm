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
		if (selectionIndex != -1) {
			TableItem item = card.getTable().getItem(selectionIndex);
			if (item != null) {
				String key = (String) item.getData();
				IResourceGetter resourceGetter = Functions.call(card.cardConfig().resourceGetterFn, card.cardType());
				final MenuItem menuItem = optionallyCreate(card, resourceGetter, menu, event, key);
				if (menuItem != null)
					menuItem.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							execute(card, menuItem);
						}
					});
			}
		}
	}

}
