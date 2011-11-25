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

public class ViewContentsMenuHandler implements ICardMenuItemHandler {

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		String cardContentFieldName = IResourceGetter.Utils.getOrNull(card.cardConfig().resourceGetterFn, key, CardConstants.cardContentField);
		if (cardContentFieldName != null) {
			String cardContentMenuPattern = resourceGetter.getStringOrNull(CardConstants.cardContentMenu);
			if (cardContentMenuPattern != null) {
				MenuItem viewContents = new MenuItem(menu, SWT.NULL);
				viewContents.setText(MessageFormat.format(cardContentMenuPattern, key));
			}
		}
		return null;
	}

	@Override
	public void execute(ICard card, MenuItem item) {
		// TODO Auto-generated method stub

	}

}
