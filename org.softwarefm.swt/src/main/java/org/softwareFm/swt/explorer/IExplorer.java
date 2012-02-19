/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer;

import java.io.File;
import java.util.List;

import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.swt.browser.IBrowserCompositeBuilder;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardChangedListener;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.card.RightClickCategoryResult;
import org.softwareFm.swt.comments.ICommentWriter;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.swt.explorer.internal.Explorer;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.timeline.IPlayListGetter;
import org.softwareFm.swt.timeline.ITimeLine;

public interface IExplorer extends IBrowserCompositeBuilder, ITimeLine, IHasCardConfig {

	void displayCard(String url, ICardAndCollectionDataStoreVisitor visitor);

	void displayUnrecognisedJar(File file, String digest, String projectName);

	void displayNotAJar();

	void displayHelpTextFor(final ICard card, final String key);

	void displayComments(ICard card);

	void displayHelpText(String cardType, String url);

	void addCardListener(ICardChangedListener listener);

	void showContents();

	void showAddSnippetEditor(final ICard card);

	void showAddCollectionItemEditor(final ICard card, final RightClickCategoryResult result);

	void showRandomContent(ICard card);

	void editSnippet(ICard card, String key);

	void showRandomSnippetFor(String artifactUrl);

	void showDebug(String ripperResult);

	void showPeople(String groupId, String artifactId);

	void edit(ICard card, String key);

	void addExplorerListener(IExplorerListener listener);

	void removeExplorerListener(IExplorerListener listener);

	void clearCaches();

	ICardHolder getCardHolder();

	void showMySoftwareFm();

	UserData getUserData();

	public static class Utils {

		public static IExplorer explorer(IMasterDetailSocial masterDetailSocial, IUserReader userReader, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, CardConfig cardConfig, List<String> rootUrls, IPlayListGetter playListGetter, IServiceExecutor service, ILoginStrategy loginStrategy, IShowMyData showMyData, IShowMyGroups showMyGroups, IShowMyPeople showMyPeople, IUserDataManager userDataManager, ICommentWriter commentWriter) {
			return new Explorer(cardConfig, rootUrls, masterDetailSocial, service, userReader, userMembershipReader, groupsReader, playListGetter, loginStrategy, showMyData, showMyGroups, showMyPeople, userDataManager, commentWriter);
		}
	}

}