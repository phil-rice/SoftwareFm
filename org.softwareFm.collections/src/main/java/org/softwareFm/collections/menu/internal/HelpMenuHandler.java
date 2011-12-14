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
