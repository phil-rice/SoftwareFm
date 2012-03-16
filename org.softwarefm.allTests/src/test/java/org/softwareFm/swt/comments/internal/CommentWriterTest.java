package org.softwareFm.swt.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.client.http.constants.CommentConstants;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.swt.comments.CommentsReaderLocal;
import org.softwareFm.swt.comments.ICommentWriter;

public class CommentWriterTest extends AbstractProcessorDatabaseIntegrationTests {
	private static final String moniker = "moniker";
	private ICommentWriter commentWriter;
	private final String softwareFmId = "someNewSoftwareFmId0";
	private String userCrypto;
	private CommentsReaderLocal commentsReader;
	private final String text1 = "someCommentText1";
	private final String text2 = "someCommentText2";
	private final String text3 = "someCommentText3";
	private IUserReader userReader;

	public void testGlobalComments() {
		checkAddGlobalComment(text1, text1);
		checkAddGlobalComment(text2, text1, text2);
		checkAddGlobalComment(text3, text1, text2, text3);
	}

	public void testMyComments() {
		checkAddMyComment(text1, text1);
		checkAddMyComment(text2, text1, text2);
		checkAddMyComment(text3, text1, text2, text3);
	}

	public void testGroupComments() {
		groups.setGroupProperty(groupId, groupCryptoKey, CommentConstants.commentCryptoKey, Crypto.makeKey());
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "groupName");
		membershipForServer.addMembership(softwareFmId, userCrypto, groupId, groupCryptoKey, "someStatus");
		
		checkAddGroupComment(text1, text1);
		checkAddGroupComment(text2, text1, text2);
		checkAddGroupComment(text3, text1, text2, text3);
	}

	protected void checkAddGlobalComment(String text, String... expectedText) {
		checkAdd("someSource", text, new Callable<ICommentDefn>() {
			@Override
			public ICommentDefn call() throws Exception {
				return ICommentDefn.Utils.everyoneInitial("a/b");
			}
		}, new Callable<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> call() throws Exception {
				return commentsReader.globalComments("a/b","someSource");
			}
		}, expectedText);
	}

	protected void checkAddMyComment(String text, String... expectedText) {
		checkAdd("someSource", text, new Callable<ICommentDefn>() {
			@Override
			public ICommentDefn call() throws Exception {
				return ICommentDefn.Utils.myInitial(userReader, softwareFmId, userCrypto, "a/b");
			}
		}, new Callable<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> call() throws Exception {
				return commentsReader.myComments("a/b", softwareFmId, userCrypto,"someSource");
			}
		}, expectedText);
	}

	protected void checkAddGroupComment(String text, String... expectedText) {
		checkAdd("groupName", text, new Callable<ICommentDefn>() {
			@Override
			public ICommentDefn call() throws Exception {
				return ICommentDefn.Utils.groupInitial(groups, groupId, groupCryptoKey, "a/b");
			}
		}, new Callable<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> call() throws Exception {
				return commentsReader.groupComments("a/b", softwareFmId, userCrypto);
			}
		}, expectedText);
	}

	protected void checkAdd(final String source, String text, Callable<ICommentDefn> makeDefinition, Callable<List<Map<String, Object>>> actualGetter, String... expectedText) {
		commentWriter.addComment(softwareFmId, userCrypto, Callables.call(makeDefinition), text);
		List<Map<String, Object>> globalComments = Callables.call(actualGetter);
		List<Map<String, Object>> expected = Lists.map(Arrays.asList(expectedText), new IFunction1<String, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(String from) throws Exception {
				return comment(source, from);
			}
		});
		assertEquals(expected, globalComments);
		
	}

	protected Map<String, Object> comment(String source, String text) {
		return Maps.makeMap(CommentConstants.sourceKey, source, CommentConstants.textKey, text, LoginConstants.softwareFmIdKey, softwareFmId, CommentConstants.creatorKey, moniker, CommentConstants.timeKey, 1000l);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		remoteOperations.init("a");

		userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		commentWriter = ICommentWriter.Utils.commentWriter(getHttpClient(), CommonConstants.testTimeOutMs, gitLocal);
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
		IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
		userReader = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
		UserMembershipReaderForLocal userMembershipReader = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, userReader);
		IGroupsReader groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);
		commentsReader = new CommentsReaderLocal(gitLocal, userReader, userMembershipReader, groupsReader);

		user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, moniker);
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, Crypto.makeKey());
	}
}
