/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ExplorerView extends AbstractExplorerView {

	@Override
	protected void configurePopupMenu(String popupMenuId, IExplorer explorer) {
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, popupMenuId);
	}

	@Override
	protected void selectionOccured(final CardConfig cardConfig, final IExplorer explorer, final IResourceGetter resourceGetter, final BindingRipperResult ripperResult) {
		String fileExtension = ripperResult.path.getFileExtension();
		if (!fileExtension.equals("jar")){
			explorer.displayNotAJar();
			return;
		}
		final String hexDigest = ripperResult.hexDigest;
		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
		String jarUrl = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));

		
		cardConfig.cardDataStore.processDataFor(jarUrl, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String jarUrl, Map<String, Object> result) throws Exception {
				String artifactUrl = makeUrl(ripperResult, cardConfig, result);
				explorer.displayCard(artifactUrl, new CardAndCollectionDataStoreAdapter());
				explorer.selectAndNext(artifactUrl);
				return null;
			}

			@Override
			public Void noData(String url) throws Exception {
				if (ripperResult != null && ripperResult.path != null)
					processNoData(cardConfig, explorer, resourceGetter, ripperResult);
				return null;
			}

		});
	}

	protected String makeUrl(BindingRipperResult ripperResult, final CardConfig cardConfig, Map<String, Object> result) {
		IUrlGenerator cardUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
		String artifactUrl = cardUrlGenerator.findUrlFor(result);
		return artifactUrl;
	}
}