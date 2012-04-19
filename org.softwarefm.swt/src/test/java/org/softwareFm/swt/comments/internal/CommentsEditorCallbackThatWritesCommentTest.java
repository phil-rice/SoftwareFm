/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;
import org.softwareFm.crowdsource.api.ApiConfig;
import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public class CommentsEditorCallbackThatWritesCommentTest extends TestCase {

	private Runnable whenFinished;
	private IUserReader userReader;
	private IGroupsReader groupsReader;
	private IComments commentWriter;
	private final String softwareFmId = "sfmId1";
	private final String userCrypto = Crypto.makeKey();
	private final String url = "url";
	private final String text = "text";
	private final String groupId = "groupId1";
	private final String groupCrypto = Crypto.makeKey();

	@SuppressWarnings("unchecked")
	private final List<Map<String, Object>> groupsData = Arrays.asList(//
			Maps.stringObjectMap(GroupConstants.groupIdKey, "groupIdBefore", GroupConstants.groupCryptoKey, Crypto.makeKey()),//
			Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto), //
			Maps.stringObjectMap(GroupConstants.groupIdKey, "groupIdAfter", GroupConstants.groupCryptoKey, Crypto.makeKey()));
	private CommentsEditorCallbackThatWritesComment callback;
	private IUserAndGroupsContainer container;

	@Test
	public void testCancel() {
		whenFinished.run();
		EasyMock.replay(userReader, groupsReader, commentWriter, whenFinished);

		callback.cancel();
	}

	public void testEveryone() {
		whenFinished.run();
		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.everyoneInitial(url), text);
		EasyMock.replay(userReader, groupsReader, commentWriter, whenFinished);

		callback.everyoneComment(url, text);
	}

	public void testMy() {
		whenFinished.run();
		EasyMock.expect(userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey)).andReturn(Crypto.makeKey()).times(2);
		EasyMock.replay(userReader);

		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.myInitial(container, softwareFmId, userCrypto, url), text);
		EasyMock.replay(groupsReader, commentWriter, whenFinished);

		callback.youComment(url, text);
	}

	public void testGroup() {
		whenFinished.run();
		String groupCommentCrypto = Crypto.makeKey();
		EasyMock.expect(groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey)).andReturn(groupCommentCrypto).times(2);
		EasyMock.replay(groupsReader);

		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.groupInitial(container, groupId, groupCrypto, url), text);
		EasyMock.replay(userReader, commentWriter, whenFinished);

		callback.groupComment(url, 1, text);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		whenFinished = Runnables.count();
		userReader = EasyMock.createMock(IUserReader.class);
		groupsReader = EasyMock.createMock(IGroupsReader.class);
		commentWriter = EasyMock.createMock(IComments.class);
		whenFinished = EasyMock.createMock(Runnable.class);
		ICrowdSourcedApi api = ICrowdSourcedApi.Utils.forTests(new IExtraReaderWriterConfigurator<ApiConfig>() {
			@Override
			public void builder(IContainerBuilder builder, ApiConfig apiConfig) {
				builder.register(IUserReader.class, userReader);
				builder.register(IGroupsReader.class, groupsReader);
				builder.register(IComments.class, commentWriter);
			}
		}, ITransactionManager.Utils.standard(CommonConstants.threadPoolSizeForTests, CommonConstants.testTimeOutMs), null);
		container = api.makeUserAndGroupsContainer();
		callback = new CommentsEditorCallbackThatWritesComment(container, softwareFmId, userCrypto, groupsData, whenFinished);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(userReader, groupsReader, commentWriter, whenFinished);
	}
}