package org.softwareFm.server.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.comments.AbstractCommentsReaderTest;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.softwareFmServer.GroupsForServer;
import org.softwareFm.softwareFmServer.UserMembershipForServer;

public class CommentsForServerTest extends AbstractCommentsReaderTest {

	private IComments comments;

	@SuppressWarnings("unchecked")
	public void testAddGlobalComments() {
		remoteOperations.init("a");
		user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, "moniker");

		checkAddComment(comment1, ICommentDefn.Utils.everyoneInitial("a/b"), comment1);
		checkAddComment(comment2, ICommentDefn.Utils.everyoneInitial("a/b"), comment1, comment2);

		assertEquals(Arrays.asList(comment1, comment2), reader.globalComments("a/b"));
		assertEquals(Arrays.asList(),  reader.myComments("a/b", softwareFmId, userCrypto));
	}

	@SuppressWarnings("unchecked")
	public void testAddMyComments() {
		remoteOperations.init("a");
		user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, "moniker");

		checkAddComment(comment1, ICommentDefn.Utils.myInitial(user, softwareFmId, userCrypto, "a/b"), comment1);
		checkAddComment(comment2, ICommentDefn.Utils.myInitial(user, softwareFmId, userCrypto, "a/b"), comment1, comment2);

		assertEquals(Arrays.asList(), reader.globalComments("a/b"));
		assertEquals(Arrays.asList(comment1, comment2), reader.myComments("a/b", softwareFmId, userCrypto));
	}
	@SuppressWarnings("unchecked")
	public void testAddGroupComments() {
		remoteOperations.init("a");
		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey, Crypto.makeKey());
		user.setUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey, "moniker");
		Map<String, String> idToCrypto = setUpGroups("groupId1","groupId2", "groupId3");
		
		String group1Crypto = idToCrypto.get("groupId1");
		checkAddComment(comment1, ICommentDefn.Utils.groupInitial(groups, "groupId1", group1Crypto,"a/b"), comment1);
		checkAddComment(comment2, ICommentDefn.Utils.groupInitial(groups, "groupId1", group1Crypto,"a/b"), comment1, comment2);
		
		assertEquals(Arrays.asList(), reader.globalComments("a/b"));
		assertEquals(Arrays.asList(), reader.myComments("a/b", softwareFmId, userCrypto));
		assertEquals(Arrays.asList(comment1, comment2), reader.groupComments("a/b", softwareFmId, userCrypto));
	}

	protected void checkAddComment(Map<String, Object> comment, ICommentDefn defn, Map<String, Object>... expected) {
		comments.add(softwareFmId, userCrypto, defn, (String) comment.get(CommentConstants.textKey));
		assertEquals(Arrays.asList(expected), ICommentsReader.Utils.allComments(reader, "a/b", softwareFmId, userCrypto));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		comments = (IComments) reader;
	}

	private final IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);

	@Override
	protected ICommentsReader makeReader(IUser user, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, String softwareFmId, String usersCrypto, String commentsCrypto) {
		return new CommentsForServer(remoteOperations, user, userMembershipReader, groupsReader, Callables.valueFromList(time1, time2));
	}

	@Override
	protected IUser makeUser() {
		return ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, Strings.firstNSegments(3), Collections.<String, Callable<Object>> emptyMap());
	}

	@Override
	protected IUserMembership makeUserMembership(IUser user, String softwareFmId, String crypto) {
		return new UserMembershipForServer(userUrlGenerator, user, remoteOperations, Strings.firstNSegments(3));
	}

	@Override
	protected IGroups makeGroups() {
		return new GroupsForServer(GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix), remoteOperations, Strings.firstNSegments(3));
	}

}
