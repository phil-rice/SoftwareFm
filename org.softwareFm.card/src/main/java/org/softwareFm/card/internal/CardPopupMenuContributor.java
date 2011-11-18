package org.softwareFm.card.internal;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IPopupMenuContributor;
import org.softwareFm.card.api.RightClickCategoryResult;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardPopupMenuContributor implements IPopupMenuContributor<ICard> {

	@Override
	public void contributeTo(Event event, Menu menu, final ICard card) {
		Table table = (Table) event.widget;
		Point location = new Point(event.x, event.y);
		Point inMySpace = table.toControl(location);
		TableItem item = table.getItem(inMySpace);
		String key = (String) (item == null ? null : item.getData());
		IResourceGetter resourceGetter = CardConfig.resourceGetter(card);
		final RightClickCategoryResult categorisation = card.cardConfig().rightClickCategoriser.categorise(card.url(), card.data(), key);
		switch (categorisation.itemType) {
		case IS_COLLECTION:
		case ROOT_COLLECTION:
			MenuItem add = new MenuItem(menu, SWT.NULL);
			String addCollectionPattern = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemAddCollection);
			add.setText(MessageFormat.format(addCollectionPattern, categorisation.collectionName));
			add.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					CardConfig cardConfig = card.cardConfig();
					cardConfig.addItemProcessor.process(categorisation);
				}
			});
			break;
		default:
		}
		if (menu.getItemCount() > 0)
			new MenuItem(menu, SWT.SEPARATOR);
		MenuItem addGroup = new MenuItem(menu, SWT.NULL);
		addGroup.setText(IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.menuItemAddArtifact));
		addGroup.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("add group");
			}
		});
	}
}
