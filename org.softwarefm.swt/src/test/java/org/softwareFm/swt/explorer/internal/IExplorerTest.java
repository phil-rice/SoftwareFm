/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.constants.CommentConstants;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.images.general.GeneralAnchor;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardHolderForTests;
import org.softwareFm.swt.comments.ICommentWriter;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.timeline.IPlayListGetter;

public class IExplorerTest extends SwtTest {

	private IServiceExecutor service;

	public void testConstructorCreatesExplorerAndCardHolderIsSetUp() {
		CardConfig cardConfig = addNeededResources(CardDataStoreFixture.syncCardConfig(display));
		IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(shell);
		try {
			List<String> rootUrls = Arrays.asList("rootUrl");
			IPlayListGetter playListGetter = IPlayListGetter.Utils.noPlayListGetter();
			IUserReader userReader = IUserReader.Utils.exceptionUserReader();
			IUserMembershipReader userMembershipReader = new UserMembershipReaderForLocal(null, null, userReader);
			LocalGroupsReader groupsReader = new LocalGroupsReader(null, null);

			Explorer explorer = (Explorer) IExplorer.Utils.explorer(//
					masterDetailSocial, userReader, userMembershipReader, groupsReader, cardConfig, rootUrls, playListGetter, service, //
					ILoginStrategy.Utils.noLoginStrategy(), //
					IShowMyData.Utils.exceptionShowMyData(),//
					IShowMyGroups.Utils.exceptionShowMyGroups(),//
					IShowMyPeople.Utils.exceptionShowMyPeople(),//
					IUserDataManager.Utils.userDataManager(), //
					ICommentWriter.Utils.exceptionCommentWriter(),//
					ICommentsReader.Utils.exceptionCommentsReader(), Callables.<Long>exceptionIfCalled());
			ICardHolderForTests cardHolder = (ICardHolderForTests) explorer.cardHolder;  
			assertEquals(rootUrls, cardHolder.getRootUrls());
			assertEquals(cardConfig, cardHolder.getCardConfig());
		} finally {
			dispatchUntilQueueEmpty();
			cardConfig.dispose();
			masterDetailSocial.dispose();
		}
	}

	public static CardConfig addNeededResources(CardConfig raw) {
		IResourceGetter resourceGetter = Functions.call(raw.resourceGetterFn, null);
		IResourceGetter withNeeded = resourceGetter.with(new ResourceGetterMock(//
				CollectionConstants.addCommentButtonTitle, "Add Comment",//
				CollectionConstants.addCommentButtonImage, GeneralAnchor.commentAdd,//
				CommentConstants.tableCreatorColumnTitle, "creatorTitle",//
				CommentConstants.tableSourceColumnTitle, "sourceTitle",//
				CommentConstants.tableTextColumnTitle, "textTitle",//
				CollectionConstants.commentsNoTitle, "No comments",//
				CollectionConstants.commentsTitle, "Comments"));
		return raw.withResourceGetterFn(Functions.<String, IResourceGetter> constant(withNeeded));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = IServiceExecutor.Utils.defaultExecutor();
	}

	@Override
	protected void tearDown() throws Exception {
		service.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		super.tearDown();
	}

}