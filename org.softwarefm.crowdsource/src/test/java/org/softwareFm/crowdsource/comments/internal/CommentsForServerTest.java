package org.softwareFm.crowdsource.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

		getServerContainer().accessComments(new ICallback<IComments>() {
			@Override
			public void process(IComments reader) throws Exception {
				assertEquals(addSource(CommentConstants.globalSource, comment1, comment2), reader.globalComments("a/b", CommentConstants.globalSource));
				assertEquals(Arrays.asList(), reader.myComments("a/b", softwareFmId, userKey0, CommentConstants.mySource));
			}
		}).get();
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

		serverContainer.accessComments(new ICallback<IComments>() {
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

		serverContainer.accessComments(new ICallback<IComments>() {
			@Override
			public void process(IComments comments) throws Exception {
				assertEquals(Arrays.asList(), comments.globalComments("a/b", CommentConstants.globalSource));
				assertEquals(Arrays.asList(), comments.myComments("a/b", softwareFmId, userKey0, CommentConstants.mySource));
				assertEquals(addSource("groupId1Name", comment1, comment2), comments.groupComments("a/b", softwareFmId, userKey0));
			}
		}).get();
	}

	protected void checkAddComment(final Map<String, Object> comment, final String source, final ICommentDefn defn, final Map<String, Object>... rawExpected) {
		getServerContainer().accessComments(new ICallback<IComments>() {
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
