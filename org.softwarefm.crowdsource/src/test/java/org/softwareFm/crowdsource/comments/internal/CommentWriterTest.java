package org.softwareFm.crowdsource.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class CommentWriterTest extends AbstractProcessorDatabaseIntegrationTests {
	private String softwareFmId;
	private final String text1 = "someCommentText1";
	private final String text2 = "someCommentText2";
	private final String text3 = "someCommentText3";
	private String groupId;

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
		getServerUserAndGroupsContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				groups.setGroupProperty(groupId, groupCryptoKey0, CommentConstants.commentCryptoKey, Crypto.makeKey());
				groups.setGroupProperty(groupId, groupCryptoKey0, GroupConstants.groupNameKey, "groupName");
				membershipForServer.addMembership(softwareFmId, userKey0, groupId, groupCryptoKey0, "someStatus");
			}
		});

		checkAddGroupComment(text1, text1);
		checkAddGroupComment(text2, text1, text2);
		checkAddGroupComment(text3, text1, text2, text3);
	}

	protected void checkAddGlobalComment(final String text, final String... expectedText) {
		ICrowdSourcedReaderApi localReaderApi = getLocalApi().makeContainer();
		localReaderApi.accessCommentsReader(new IFunction1<ICommentsReader, Void>() {
			@Override
			public Void apply(final ICommentsReader commentsReader) throws Exception {
				checkAdd("someSource", text, new Callable<ICommentDefn>() {
					@Override
					public ICommentDefn call() throws Exception {
						return ICommentDefn.Utils.everyoneInitial("a/b");
					}
				}, new Callable<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> call() throws Exception {
						return commentsReader.globalComments("a/b", "someSource");
					}
				}, expectedText);
				return null;
			}
		});
	}

	protected void checkAddMyComment(final String text, final String... expectedText) {
		 final IUserAndGroupsContainer container = getLocalUserAndGroupsContainer();
		 container.access(ICommentsReader.class, IUserReader.class, new IFunction2<ICommentsReader, IUserReader, Void>() {
			@Override
			public Void apply(final ICommentsReader commentsReader, final IUserReader userReader) throws Exception {
				checkAdd("someSource", text, new Callable<ICommentDefn>() {
					@Override
					public ICommentDefn call() throws Exception {
						return ICommentDefn.Utils.myInitial(container, softwareFmId, userKey0, "a/b");
					}
				}, new Callable<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> call() throws Exception {
						return commentsReader.myComments("a/b", softwareFmId, userKey0, "someSource");
					}
				}, expectedText);
				return null;
			}
		});
	}

	protected void checkAddGroupComment(final String text, final String... expectedText) {
		final IUserAndGroupsContainer container = getLocalUserAndGroupsContainer();
		container.access(ICommentsReader.class, IGroupsReader.class, new IFunction2<ICommentsReader, IGroupsReader, Void>() {
			@Override
			public Void apply(final ICommentsReader commentsReader, final IGroupsReader groupsReader) throws Exception {

				checkAdd("groupName", text, new Callable<ICommentDefn>() {
					@Override
					public ICommentDefn call() throws Exception {
						return ICommentDefn.Utils.groupInitial(container, groupId, groupCryptoKey0, "a/b");
					}
				}, new Callable<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> call() throws Exception {
						return commentsReader.groupComments("a/b", softwareFmId, userKey0);
					}
				}, expectedText);
				return null;
			}
		});
	}

	protected void checkAdd(final String source, final String text, final Callable<ICommentDefn> makeDefinition, final Callable<List<Map<String, Object>>> actualGetter, final String... expectedText) {
		IContainer localWriterApi = getLocalApi().makeContainer();
		localWriterApi.modifyComments(new ICallback<IComments>() {
			@Override
			public void process(IComments comments) throws Exception {
				comments.addComment(softwareFmId, userKey0, Callables.call(makeDefinition), text);
				List<Map<String, Object>> globalComments = Callables.call(actualGetter);
				List<Map<String, Object>> expected = Lists.map(Arrays.asList(expectedText), new IFunction1<String, Map<String, Object>>() {
					private long time = 0;
					@Override
					public Map<String, Object> apply(String from) throws Exception {
						return comment(source, from, time += 1000);
					}
				});
				assertEquals(expected, globalComments);
			}
		});

	}

	protected Map<String, Object> comment(String source, String text, long time) {
		return Maps.makeMap(CommentConstants.sourceKey, source, CommentConstants.textKey, text, LoginConstants.softwareFmIdKey, softwareFmId, CommentConstants.creatorKey, someMoniker, CommentConstants.timeKey, time);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		remoteOperations.init("a");
		truncateUsersTable();
		softwareFmId = createUser();
		groupId = createGroup("someGroupName", groupCryptoKey0);
	}
}
