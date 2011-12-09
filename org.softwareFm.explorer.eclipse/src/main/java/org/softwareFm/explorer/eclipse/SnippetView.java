/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.ExpressionData;
import org.softwareFm.jdtBinding.api.IExpressionCategoriser;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SnippetView extends AbstractExplorerView {

	IExpressionCategoriser categoriser = IExpressionCategoriser.Utils.categoriser();

	@Override
	protected void selectionOccured(CardConfig cardConfig, final IExplorer explorer, IResourceGetter resourceGetter, BindingRipperResult ripperResult) {
		final String artifactUrl = makeUrl(ripperResult, cardConfig);
		if (artifactUrl != null)
			explorer.displayCard(artifactUrl, new CardAndCollectionDataStoreAdapter() {
				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) {
					explorer.showRandomSnippetFor(artifactUrl);
				}
			});
	}

	@Override
	protected void configurePopupMenu(String popupMenuId, IExplorer explorer) {
		ICardMenuItemHandler.Utils.addSnippetMenuItemHandlers(explorer, popupMenuId);
	}

	protected String makeUrl(BindingRipperResult ripperResult, final CardConfig cardConfig) {
		ExpressionData key = categoriser.categorise(ripperResult.expression);
		if (key == null)
			return null;
		String baseUrl = CollectionConstants.rootUrl + "/" + key.classKey;
		if (key.methodKey != null && key.methodKey.length() > 0) {
			String result = baseUrl + "/method/" + key.methodKey;
			return result + "/snippet";
		} else
			return baseUrl + "/snippet";
	}



}