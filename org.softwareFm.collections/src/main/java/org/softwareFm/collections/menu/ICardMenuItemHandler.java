/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.menu;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.internal.AddItemToCollectionMenuHandler;
import org.softwareFm.collections.menu.internal.AddNewArtifactMenuHandler;
import org.softwareFm.collections.menu.internal.AddNewSnippetMenuHandler;
import org.softwareFm.collections.menu.internal.EditTextMenuHandler;
import org.softwareFm.collections.menu.internal.OptionalSeparatorMenuHandler;
import org.softwareFm.collections.menu.internal.ViewActualContentsMenuHandler;
import org.softwareFm.collections.menu.internal.ViewCardsMenuHandler;
import org.softwareFm.collections.menu.internal.ViewTextMenuHandler;
import org.softwareFm.display.menu.IPopupMenuContributor;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ICardMenuItemHandler extends IPopupMenuContributor<ICard> {

	MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key);

	void execute(ICard card, String key, MenuItem item);

	public static class Utils {

		public static void addExplorerMenuItemHandlers(IExplorer explorer, String popupMenuId) {
			CardConfig cardConfig = explorer.getCardConfig();
			cardConfig.popupMenuService.registerMenuId(popupMenuId);
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.editText(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewText(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewCards(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addItemToCollection(explorer));//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.optionalSeparator());//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addNewArtifact(explorer));
		}
		public static void addSnippetMenuItemHandlers(IExplorer explorer, String popupMenuId) {
			CardConfig cardConfig = explorer.getCardConfig();
			cardConfig.popupMenuService.registerMenuId(popupMenuId);
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewActualContents(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addNewSnippet(explorer));//
		}

		public static ICardMenuItemHandler addItemToCollection(IExplorer explorer) {
			return new AddItemToCollectionMenuHandler(explorer);
		}

		public static ICardMenuItemHandler addNewArtifact(IExplorer explorer) {
			return new AddNewArtifactMenuHandler(explorer);
		}

		public static ICardMenuItemHandler optionalSeparator() {
			return new OptionalSeparatorMenuHandler();
		}

		public static ICardMenuItemHandler viewActualContents(IExplorer explorer) {
			return new ViewActualContentsMenuHandler(explorer);
		}
		
		public static ICardMenuItemHandler editText(IExplorer explorer) {
			return new EditTextMenuHandler(explorer);
		}
		public static ICardMenuItemHandler viewText(IExplorer explorer) {
			return new ViewTextMenuHandler(explorer);
		}
		public static ICardMenuItemHandler viewCards(IExplorer explorer) {
			return new ViewCardsMenuHandler(explorer);
		}
		public static ICardMenuItemHandler addNewSnippet(IExplorer explorer) {
			return new AddNewSnippetMenuHandler(explorer);
		}

	}

}