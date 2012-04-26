/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class CommentsForServerTest extends AbstractCommentsReaderTest {

	@Override
	protected ICrowdSourcedApi getApi() {
		return getServerApi();
	}

	@SuppressWarnings("unchecked")
	public void testAddGlobalComments() {
		remoteOperations.init("a");
		getServerUserAndGroupsContainer().accessUser(new ICallback<IUser>() {
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(softwareFmId, userKey0, LoginConstants.monikerKey, "moniker");
			}
		}).get();

		checkAddComment(comment1, CommentConstants.globalSource, ICommentDefn.Utils.everyoneInitial("a/b"), comment1);
		checkAddComment(comment2, CommentConstants.globalSource, ICommentDefn.Utils.everyoneInitial("a/b"), comment1, comment2);

		getServerContainer().access(IComments.class, new ICallback<IComments>() {
			@Override
			public void process(IComments reader) throws Exception {
				assertEquals(addSource(CommentConstants.globalSource, comment1, comment2), reader.globalComments("a/b", CommentConstants.globalSource));
				assertEquals(Arrays.asList(), reader.myComments("a/b", softwareFmId, userKey0, CommentConstants.mySource));
			}
		}).get();
	}

	@Override
	protected Callable<Long> getTimeGetter() {
		return Callables.valueFromList(1000L, 2000L, 3000L, 4000L, 5000L);
	}

	@SuppressWarnings("unchecked")
	public void testAddMyComments() {
		remoteOperations.init("a");
		IUserAndGroupsContainer serverContainer = getServerUserAndGroupsContainer();
		serverContainer.accessUser(new ICallback<IUser>() {
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(softwareFmId, userKey0, LoginConstants.monikerKey, "moniker");
			}
		}).get();

		checkAddComment(comment1, CommentConstants.mySource, ICommentDefn.Utils.myInitial(serverContainer, softwareFmId, userKey0, "a/b"), comment1);
		checkAddComment(comment2, CommentConstants.mySource, ICommentDefn.Utils.myInitial(serverContainer, softwareFmId, userKey0, "a/b"), comment1, comment2);

		serverContainer.access(IComments.class, new ICallback<IComments>() {
			@Override
			public void process(IComments comments) throws Exception {
				assertEquals(Arrays.asList(), comments.globalComments("a/b", CommentConstants.globalSource));
				assertEquals(addSource(CommentConstants.mySource, comment1, comment2), comments.myComments("a/b", softwareFmId, userKey0, CommentConstants.mySource));
			}
		}).get();
	}

	@SuppressWarnings("unchecked")
	public void testAddGroupComments() {
		remoteOperations.init("a");
		IUserAndGroupsContainer serverContainer = getServerUserAndGroupsContainer();
		serverContainer.accessUser(new ICallback<IUser>() {
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(softwareFmId, userKey0, GroupConstants.membershipCryptoKey, Crypto.makeKey());
				user.setUserProperty(softwareFmId, userKey0, LoginConstants.monikerKey, "moniker");

			}
		}).get();
		Map<String, String> idToCrypto = setUpGroups("groupId1", "groupId2", "groupId3");

		String group1Crypto = idToCrypto.get("groupId1");
		checkAddComment(comment1, "groupId1Name", ICommentDefn.Utils.groupInitial(serverContainer, "groupId1", group1Crypto, "a/b"), comment1);
		checkAddComment(comment2, "groupId1Name", ICommentDefn.Utils.groupInitial(serverContainer, "groupId1", group1Crypto, "a/b"), comment1, comment2);

		serverContainer.access(IComments.class, new ICallback<IComments>() {
			@Override
			public void process(IComments comments) throws Exception {
				assertEquals(Arrays.asList(), comments.globalComments("a/b", CommentConstants.globalSource));
				assertEquals(Arrays.asList(), comments.myComments("a/b", softwareFmId, userKey0, CommentConstants.mySource));
				assertEquals(addSource("groupId1Name", comment1, comment2), comments.groupComments("a/b", softwareFmId, userKey0));
			}
		}).get();
	}

	protected void checkAddComment(final Map<String, Object> comment, final String source, final ICommentDefn defn, final Map<String, Object>... rawExpected) {
		getServerContainer().access(IComments.class, new ICallback<IComments>() {
			@Override
			public void process(IComments comments) throws Exception {
				comments.addComment(softwareFmId, userKey0, defn, (String) comment.get(CommentConstants.textKey));
				List<Map<String, Object>> expected = addSource(source, rawExpected);
				List<Map<String, Object>> actual = ICommentsReader.Utils.allComments(comments, "a/b", softwareFmId, userKey0, CommentConstants.globalSource, CommentConstants.mySource);
				assertEquals(expected, actual);
			}
		}).get();
	}

}