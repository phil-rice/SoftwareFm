package org.softwareFm.server.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.server.comments.CommentsForServer;
import org.softwareFm.server.internal.ServerUser;
import org.softwareFm.softwareFmServer.GroupsForServer;
import org.softwareFm.softwareFmServer.UserMembershipForServer;

public class CommentProcessorTest extends AbstractProcessCallTest<CommentProcessor> {

	private final String softwareFmId = "sfmId1";
	private final String userCrypto = Crypto.makeKey();
	private final String userCommentCrypto = Crypto.makeKey();
	String url = "a/b";

	private final String groupId = "groupId";
	private final String groupCrypto = Crypto.makeKey();
	private final String groupCommentCrypto = Crypto.makeKey();

	private IUser user;
	private IUserMembership userMembership;
	private IGroups groups;
	private IFunction1<Map<String, Object>, String> cryptoFn;
	private CommentsForServer comments;
	private final String monikor = "Monikor";

	private final Map<String, Object> comment1 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 1000l,//
			CommentConstants.textKey, "text1");
	private final Map<String, Object> comment2 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 1000l,//
			CommentConstants.textKey, "text2");
	private final Map<String, Object> comment3 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 1000l,//
			CommentConstants.textKey, "text3");

	public void testNeedsPost() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST, "someUri");
	}

	@SuppressWarnings("unchecked")
	public void testAddsComments() {
		checkAddComment(comment1, CommentConstants.globalCommentsFile, comment1);
		checkAddComment(comment2, CommentConstants.globalCommentsFile, comment1, comment2);
		checkAddComment(comment3, CommentConstants.globalCommentsFile, comment1, comment2, comment3);

		checkGlobalComments(comment1, comment2, comment3);

	}

	private void checkGlobalComments(Map<String, Object>... expected) {
		assertEquals(Lists.map(Arrays.asList(expected), Maps.<String,Object>withFn(CommentConstants.sourceKey,"someSource")), comments.globalComments(url, "someSource"));
	}

	protected void checkAddComment(Map<String, Object> comment, String filename, Map<String, Object>... expected) {
		Object text = comment.get(CommentConstants.textKey);
		IProcessResult result = processor.execute(Urls.composeWithSlash(CommentConstants.addCommandSuffix, url), //
				Maps.stringObjectMap(//
						LoginConstants.softwareFmIdKey, softwareFmId, //
						CommentConstants.textKey, Crypto.aesEncrypt(userCrypto, (String) text),//
						CommentConstants.filenameKey, filename));
		checkStringResult(result, "");
		assertEquals(Lists.map(Arrays.asList(expected), Maps.<String,Object>withFn(CommentConstants.sourceKey,"source")), ICommentsReader.Utils.allComments(comments, url, softwareFmId, userCrypto, "source", "source"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey, groupCrypto);
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, userCommentCrypto);
		user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, monikor);
		groups.setGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey, groupCommentCrypto);
		userMembership.addMembership(softwareFmId, userCrypto, groupId, groupCrypto, "someStatus");

		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
	}

	@Override
	protected CommentProcessor makeProcessor() {
		cryptoFn = Functions.mapFromKey(LoginConstants.softwareFmIdKey, softwareFmId, userCrypto);

		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
		IUrlGenerator groupsUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);

		user = new ServerUser(localOperations, userUrlGenerator, Strings.firstNSegments(3), Collections.<String, Callable<Object>> emptyMap());
		userMembership = new UserMembershipForServer(userUrlGenerator, user, remoteOperations, Strings.firstNSegments(3));
		groups = new GroupsForServer(groupsUrlGenerator, remoteOperations, Strings.firstNSegments(3));
		comments = new CommentsForServer(remoteOperations, user, userMembership, groups, Callables.<Long> value(1000l));
		return new CommentProcessor(user, userMembership, groups, comments, cryptoFn);
	}
}
