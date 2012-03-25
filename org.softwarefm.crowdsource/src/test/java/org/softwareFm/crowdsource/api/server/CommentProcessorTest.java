package org.softwareFm.crowdsource.api.server;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.comments.internal.CommentsForServer;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.server.callProcessor.internal.CommentProcessor;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class CommentProcessorTest extends AbstractProcessCallTest<CommentProcessor> {

	private final String softwareFmId = "sfmId1";
	private final String userCrypto = Crypto.makeKey();
	private final String userCommentCrypto = Crypto.makeKey();
	String url = "a/b";

	private final String groupId = "groupId";
	private final String groupCrypto = Crypto.makeKey();
	private final String groupCommentCrypto = Crypto.makeKey();

	private CommentsForServer comments;
	private final String monikor = "Monikor";

	private final Map<String, Object> comment1 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 1000l,//
			CommentConstants.textKey, "text1");
	private final Map<String, Object> comment2 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 2000l,//
			CommentConstants.textKey, "text2");
	private final Map<String, Object> comment3 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
			CommentConstants.creatorKey, monikor,//
			CommentConstants.timeKey, 3000l,//
			CommentConstants.textKey, "text3");
	private ICrowdSourcedApi api;

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
		assertEquals(Lists.map(Arrays.asList(expected), Maps.<String, Object> withFn(CommentConstants.sourceKey, "someSource")), comments.globalComments(url, "someSource"));
	}

	protected void checkAddComment(Map<String, Object> comment, String filename, Map<String, Object>... expected) {
		Object text = comment.get(CommentConstants.textKey);
		IProcessResult result = processor.execute(Urls.composeWithSlash(CommentConstants.addCommandSuffix, url), //
				Maps.stringObjectMap(//
						LoginConstants.softwareFmIdKey, softwareFmId, //
						CommentConstants.textKey, Crypto.aesEncrypt(userCrypto, (String) text),//
						CommentConstants.filenameKey, filename));
		checkStringResult(result, "");
		assertEquals(Lists.map(Arrays.asList(expected), Maps.<String, Object> withFn(CommentConstants.sourceKey, "source")), ICommentsReader.Utils.allComments(comments, url, softwareFmId, userCrypto, "source", "source"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		api.makeContainer().modifyUser(new ICallback<IUser>(){
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey, groupCrypto);
				user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, userCommentCrypto);
				user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, monikor);
			}});
		api.makeContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey, groupCommentCrypto);
				userMembership.addMembership(softwareFmId, userCrypto, groupId, groupCrypto, "someStatus");
			}});

		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
	}

	@Override
	protected CommentProcessor makeProcessor() {
		ServerConfig serverConfig = ServerConfig.serverConfigForTests(remoteRoot, IMailer.Utils.noMailer());
		api = ICrowdSourcedApi.Utils.forServer(serverConfig);
		comments = new CommentsForServer(api.makeContainer(), Callables.valueFromList(1000l, 2000l, 3000l));
		return new CommentProcessor(api.makeContainer(), IUserCryptoAccess.Utils.mock(softwareFmId, userCrypto));
	}
}
