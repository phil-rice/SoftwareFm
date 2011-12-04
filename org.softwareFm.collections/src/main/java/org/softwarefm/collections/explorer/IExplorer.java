/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer;

import java.io.File;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.collections.explorer.internal.Explorer;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.ITimeLine;
import org.softwareFm.utilities.services.IServiceExecutor;

public interface IExplorer extends IBrowserCompositeBuilder, ITimeLine, IHasCardConfig {

	void displayCard(String url, ICardAndCollectionDataStoreVisitor visitor);

	void displayUnrecognisedJar(File file, String digest);

	void displayComments(String url);

	void addCardListener(ICardChangedListener listener);

	void showContents();

	void showAddSnippetEditor(final ICard card);

	void showAddCollectionItemEditor(final ICard card, final RightClickCategoryResult result);

	void showAddNewArtifactEditor();

	void showRandomContent(ICard card);

	void showRandomSnippetFor(String artifactUrl);

	void changeState(ExplorerState state);

	public static class Utils {

		public static IExplorer explorer(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, String rootUrl, IPlayListGetter playListGetter, IServiceExecutor service) {
			return new Explorer(cardConfig, rootUrl, masterDetailSocial, service, playListGetter);
		}
	}

}