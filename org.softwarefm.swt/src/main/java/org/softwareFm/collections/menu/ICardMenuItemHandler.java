/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.menu;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.internal.AddItemToCollectionMenuHandler;
import org.softwareFm.collections.menu.internal.AddNewArtifactMenuHandler;
import org.softwareFm.collections.menu.internal.AddNewSnippetMenuHandler;
import org.softwareFm.collections.menu.internal.BrowseUrlMenuHandler;
import org.softwareFm.collections.menu.internal.EditSnippetMenuHandler;
import org.softwareFm.collections.menu.internal.EditTextInSnippetMenuHandler;
import org.softwareFm.collections.menu.internal.EditTextMenuHandler;
import org.softwareFm.collections.menu.internal.ExternalBrowseUrlMenuHandler;
import org.softwareFm.collections.menu.internal.HelpMenuHandler;
import org.softwareFm.collections.menu.internal.OptionalSeparatorMenuHandler;
import org.softwareFm.collections.menu.internal.ViewActualContentsMenuHandler;
import org.softwareFm.collections.menu.internal.ViewCardsMenuHandler;
import org.softwareFm.collections.menu.internal.ViewSnippetMenuHandler;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.display.menu.IPopupMenuContributor;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.configuration.CardConfig;

public interface ICardMenuItemHandler extends IPopupMenuContributor<ICard> {

	MenuItem optionallyCreate(ICard card, IResourceGetter resourceGetter, Menu menu, Event event, String key);

	void execute(ICard card, String key, MenuItem item);

	public static class Utils {
		public static void addSoftwareFmMenuItemHandlers(IExplorer explorer) {
			addExplorerMenuItemHandlers(explorer, "data");
			addExplorerMenuItemHandlers(explorer, "jars");
			addSnippetMenuItemHandlers(explorer, "snippet");
			addExplorerMenuItemHandlers(explorer, "jarname");
		}

		public static void addExplorerMenuItemHandlers(IExplorer explorer, String popupMenuId) {
			CardConfig cardConfig = explorer.getCardConfig();
			cardConfig.popupMenuService.registerMenuId(popupMenuId);
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.editText(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewCards(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addItemToCollection(explorer));//
			// cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.optionalSeparator());//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.browseUrl(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.externalBrowseUrl(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.help(explorer));
			// cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.optionalSeparator());//
			// cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addNewArtifact(explorer));
		}

		private static IPopupMenuContributor<ICard> help(IExplorer explorer) {
			return new HelpMenuHandler(explorer);
		}

		public static void addSnippetMenuItemHandlers(IExplorer explorer, String popupMenuId) {
			CardConfig cardConfig = explorer.getCardConfig();
			cardConfig.popupMenuService.registerMenuId(popupMenuId);
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewActualContents(explorer));
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.addNewSnippet(explorer));//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.editSnippet(explorer));//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.viewSnippet(explorer));//
			cardConfig.popupMenuService.registerContributor(popupMenuId, ICardMenuItemHandler.Utils.editTextInSnippet(explorer));//
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

		public static ICardMenuItemHandler browseUrl(IExplorer explorer) {
			return new BrowseUrlMenuHandler(explorer);
		}

		public static ICardMenuItemHandler externalBrowseUrl(IExplorer explorer) {
			return new ExternalBrowseUrlMenuHandler(explorer);
		}

		public static ICardMenuItemHandler editText(IExplorer explorer) {
			return new EditTextMenuHandler(explorer);
		}

		public static ICardMenuItemHandler viewCards(IExplorer explorer) {
			return new ViewCardsMenuHandler(explorer);
		}

		public static ICardMenuItemHandler addNewSnippet(IExplorer explorer) {
			return new AddNewSnippetMenuHandler(explorer);
		}

		public static ICardMenuItemHandler editSnippet(IExplorer explorer) {
			return new EditSnippetMenuHandler(explorer);
		}

		public static ICardMenuItemHandler viewSnippet(IExplorer explorer) {
			return new ViewSnippetMenuHandler(explorer);
		}

		public static ICardMenuItemHandler editTextInSnippet(IExplorer explorer) {
			return new EditTextInSnippetMenuHandler(explorer);
		}

	}

}