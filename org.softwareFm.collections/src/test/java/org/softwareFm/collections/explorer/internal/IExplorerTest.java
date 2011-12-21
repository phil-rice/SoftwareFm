/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.card.card.ICardHolderForTests;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.ResourceGetterMock;
import org.softwareFm.utilities.services.IServiceExecutor;

public class IExplorerTest extends SwtTest {

	private IServiceExecutor service;

	public void testConstructorCreatesExplorerAndCardHolderIsSetUp() {
		CardConfig cardConfig = addNeededResources(CardDataStoreFixture.syncCardConfig(display));
		String rootUrl = "rootUrl";
		IPlayListGetter playListGetter = IPlayListGetter.Utils.noPlayListGetter();
		IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(shell);
		Explorer explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, cardConfig, rootUrl, playListGetter, service);
		ICardHolderForTests cardHolder = (ICardHolderForTests) explorer.cardHolder;
		assertEquals(rootUrl, cardHolder.getRootUrl());
		assertEquals(cardConfig, cardHolder.getCardConfig());
	}

	public static  CardConfig addNeededResources(CardConfig raw) {
		IResourceGetter resourceGetter = Functions.call(raw.resourceGetterFn, null);
		IResourceGetter withNeeded = resourceGetter.with(new ResourceGetterMock(//
				CollectionConstants.addCommentButtonTitle, "Add Comment",//
				CollectionConstants.addCommentButtonImage, GeneralAnchor.commentAdd,//
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
		service.shutdownAndAwaitTermination(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		super.tearDown();
	}

}