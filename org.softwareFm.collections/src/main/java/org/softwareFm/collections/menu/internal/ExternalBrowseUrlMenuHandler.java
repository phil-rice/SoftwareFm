package org.softwareFm.collections.menu.internal;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.AbstractCardMenuHandler;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Urls;

public class ExternalBrowseUrlMenuHandler extends AbstractCardMenuHandler {

	public ExternalBrowseUrlMenuHandler(IExplorer explorer) {
		super(explorer);
	}

	@Override
	public MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key) {
		Object value = card.data().get(key);
		if (value instanceof String) {
			String string = (String) value;
			if (Urls.isUrl(string)) {
				MenuItem item = new MenuItem(menu, SWT.NULL);
				item.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemExternalBrowseText));
				return item;
			}
		}

		return null;
	}

	@Override
	public void execute(ICard card, String key, MenuItem item) {
		try {
			String url = (String) card.data().get(key);
			Desktop.getDesktop().browse(new URI(url.trim()));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
